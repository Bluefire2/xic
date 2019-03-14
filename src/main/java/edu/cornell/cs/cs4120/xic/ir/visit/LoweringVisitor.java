package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Iterator;

public class LoweringVisitor extends IRVisitor {
    private int tempcounter;
    private int labelcounter;

    private String newTemp() {
        return String.format("_lir_t%d", (tempcounter++));
    }

    private String newLabel(){
        return String.format("_lir_l%d", (labelcounter++));
    }

    private class BasicBlock {
        List<IRStmt> statements;
        boolean marked;
        String label;
        String jumpTo;
        boolean canDeleteLabel;


        BasicBlock (IRLabel l) {
            statements = new ArrayList<>();
            statements.add(l);
            label = l.name();
            marked = false;
            canDeleteLabel = true;
        }

        void addStmt(IRStmt stmt) {
            statements.add(stmt);
        }

        void mark() { marked = true; }

        void doNotDeleteLabel() { canDeleteLabel = false; }

        void deleteLabel() { statements.remove(0); }

    }

    public LoweringVisitor(IRNodeFactory inf) {
        super(inf);
        tempcounter = 0;
    }

    //helper function for traces (start with [value] and continue tracing
    //modifies ordered
    private void trace(BasicBlock value,
                       LinkedHashMap<String, BasicBlock> lookup,
                       List<BasicBlock> ordered) {
        if (!value.marked){ //unmarked block
            value.mark();
            ordered.add(value);

            String jumpTo = value.jumpTo;
            if (jumpTo != null){ //check if we need to jump somewhere
                BasicBlock jumpToBlock = lookup.get(jumpTo);
                if (jumpToBlock.marked){
                    //we need to explicitly jump there because the block is already in our list
                    jumpToBlock.doNotDeleteLabel();
                    value.addStmt(new IRJump(new IRName(jumpTo)));
                } else { //continue the trace if jumpTo is unmarked
                    trace(jumpToBlock, lookup, ordered);
                }
            }
        }
    }

    /**
     * Reorder basic blocks so that jumps fall through whenever possible.
     * @param root Seq of statements
     * @return new Seq with reordered statements
     */
    public IRSeq reorderBasicBlocks(IRSeq root) {
        List<IRStmt> stmts = root.stmts();
        LinkedHashMap<String, BasicBlock> lookup = new LinkedHashMap<>();
        BasicBlock currentBlock = new BasicBlock(new IRLabel("_start"));
        //Statements Pass: build basic blocks
        for (int i = 0; i < stmts.size(); i++){
            IRStmt currStmt = stmts.get(i);
            if (currStmt instanceof IRLabel){
                if (currentBlock != null) {
                    //wrap up the current block
                    lookup.put(currentBlock.label, currentBlock);
                }
                //start a new block
                currentBlock = new BasicBlock((IRLabel) currStmt);
            } else if (currStmt instanceof IRJump){
                IRJump j = (IRJump) currStmt;
                //This needs to be changed later (pa7), currently we always jump to named locs
                String name = ((IRName) j.target()).name();
                currentBlock.jumpTo = name;
                //do not add to block, but record that it should jumpTo
                lookup.put(currentBlock.label, currentBlock);
                currentBlock = null;
            } else if (currStmt instanceof IRCJump){
                IRCJump cjump = (IRCJump) currStmt;
                //if has a false label, get rid of it and add a jump target
                if (cjump.hasFalseLabel()){
                    //add a new CJump that doesn't have the false branch
                    //record it should jumpTo the false branch
                    IRCJump newcjump = new IRCJump(cjump.cond(), cjump.trueLabel());
                    currentBlock.addStmt(newcjump);
                    currentBlock.jumpTo = cjump.falseLabel();
                    lookup.put(currentBlock.label, currentBlock);
                    currentBlock = null;
                } else {//already falls through
                    currentBlock.addStmt(currStmt);
                }
            } else if (currStmt instanceof IRReturn){
                currentBlock.addStmt(currStmt);
                lookup.put(currentBlock.label, currentBlock);
                currentBlock = null;
            } else {
                if (currentBlock == null) {
                    //if block completed and next statement is not a label
                    //make a fake label so that every block has a label
                    currentBlock = new BasicBlock(new IRLabel(newLabel()));
                }
                currentBlock.addStmt(currStmt);
            }
        }
        //add the last block
        lookup.put(currentBlock.label, currentBlock);

        //none of the blocks have false CJump or Jumps now
        //we have to add these in if they can't be reordered nicely (see Trace)

        //Basic Blocks Pass 1: build ordering
        List<BasicBlock> ordered = new ArrayList<>();
        lookup.forEach((key, value) -> {
            trace(value, lookup, ordered);
        });

        List<IRStmt> newSeqContents = new ArrayList<>();
        //Basic Blocks Pass 2: delete labels and add to new seq
        for (BasicBlock block : ordered) {
            if (block.canDeleteLabel){
                //we know every block starts with a label
                block.deleteLabel();
            }
            newSeqContents.addAll(block.statements);
        }
        //return new Seq
        return new IRSeq(newSeqContents);
    }

    /**
     * Return all temps accessed by a list of nodes.
     * @param l list of IRNodes
     * @return list of IRTemps
     */
    private ArrayList<String> getTemps(List<IRNode> l) {
        ArrayList<String> temps = new ArrayList<>();
        for (IRNode n : l) {
            if (n instanceof IRTemp) {
                temps.add(((IRTemp) n).name());
            }
        }
        return temps;
    }

    /**
     * Determine whether two expressions commute according to the following
     * rules: they do not access the same temps, and neither expression contains
     * a memory instruction.
     * @param e1 first expression
     * @param e2 second expression
     * @return true if e1 and e2 commute, false otherwise
     */
    private boolean ifExprsCommute(IRExpr e1, IRExpr e2) {
        List<IRNode> e1Children = e1.aggregateChildren(new ListChildrenVisitor());
        e1Children.add(e1);
        List<IRNode> e2Children = e2.aggregateChildren(new ListChildrenVisitor());
        e2Children.add(e2);
        //Check MEM
        for (IRNode n : e2Children) {
            if (n instanceof IRMem) return false;
        }
        //Check TEMPs
        HashSet<String> tempsE1 = new HashSet<>(getTemps(e1Children));
        for (String t : getTemps(e2Children)) {
            if (tempsE1.contains(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (n_ instanceof IRBinOp) return lower((IRBinOp) n_);
        if (n_ instanceof IRCall) return lower((IRCall) n_);
        if (n_ instanceof IRCJump) return lower((IRCJump) n_);
        if (n_ instanceof IRCompUnit) return lower((IRCompUnit) n_);
        if (n_ instanceof IRConst) return lower((IRConst) n_);
        if (n_ instanceof IRESeq) return lower((IRESeq) n_);
        if (n_ instanceof IRExp) return lower((IRExp) n_);
        if (n_ instanceof IRFuncDecl) return lower((IRFuncDecl) n_);
        if (n_ instanceof IRJump) return lower((IRJump) n_);
        if (n_ instanceof IRLabel) return lower((IRLabel) n_);
        if (n_ instanceof IRMem) return lower((IRMem) n_);
        if (n_ instanceof IRMove) return lower((IRMove) n_);
        if (n_ instanceof IRName) return lower((IRName) n_);
        if (n_ instanceof IRReturn) return lower((IRReturn) n_);
        if (n_ instanceof IRSeq) return lower((IRSeq) n_);
        if (n_ instanceof IRTemp) return lower((IRTemp) n_);
        else return null; //Illegal state
    }


    public IRNode lower(IRBinOp irnode) {
        IRExpr left = irnode.left();
        IRExpr right = irnode.right();

        if (!(left instanceof IRESeq || right instanceof IRESeq)) {
            return irnode;
        } else if (ifExprsCommute(left, right)) {
            IRESeq leftSeq;
            IRESeq rightSeq;
            IRExpr e1;
            IRExpr e2;
            IRStmt s1 = null;
            IRStmt s2 = null;

            if (left instanceof IRESeq && right instanceof IRESeq) {
                leftSeq = (IRESeq) left;
                rightSeq = (IRESeq) right;
                e1 = leftSeq.expr();
                e2 = rightSeq.expr();
                s1 = leftSeq.stmt();
                s2 = rightSeq.stmt();
            } else if (left instanceof IRESeq) {
                leftSeq = (IRESeq) left;
                e1 = leftSeq.expr();
                e2 = right;
                s1 = leftSeq.stmt();
            } else {
                rightSeq = (IRESeq) right;
                e1 = left;
                e2 = rightSeq.expr();
                s2 = rightSeq.stmt();
            }

            return new IRESeq(
                    new IRSeq(s1, s2),
                    new IRBinOp(
                            irnode.opType(), e1, e2
                    ));
        } else {
            IRESeq leftSeq;
            IRESeq rightSeq;
            IRExpr e1;
            IRExpr e2;
            IRStmt s1 = null;
            IRStmt s2 = null;

            String t1 = newTemp();
            if (left instanceof IRESeq && right instanceof IRESeq) {
                leftSeq = (IRESeq) left;
                rightSeq = (IRESeq) right;
                e1 = leftSeq.expr();
                e2 = rightSeq.expr();
                s1 = leftSeq.stmt();
                s2 = rightSeq.stmt();
            } else if (left instanceof IRESeq) {
                leftSeq = (IRESeq) left;
                e1 = leftSeq.expr();
                e2 = right;
                s1 = leftSeq.stmt();
            } else {
                rightSeq = (IRESeq) right;
                e1 = left;
                e2 = rightSeq.expr();
                s2 = rightSeq.stmt();
            }

            return new IRESeq(
                    new IRSeq(
                            s1,
                            new IRMove(
                                    new IRTemp(t1), e1
                            ),
                            s2
                    ),
                    new IRBinOp(
                            irnode.opType(), new IRTemp(t1), e2
                    ));
        }
    }

    public IRNode lower(IRCall irnode) {
        List<String> tis = new ArrayList<>();
        List<IRStmt> stmts = new ArrayList<>();
        for (IRExpr ei : irnode.args()) {
            String ti = newTemp();
            tis.add(ti);

            if (ei instanceof IRESeq) {
                IRESeq ireSeq = (IRESeq) ei;
                stmts.add(ireSeq.stmt());
                stmts.add(new IRMove(
                        new IRTemp(ti), ireSeq.expr()
                ));
            } else {
                stmts.add(new IRMove(
                        new IRTemp(ti), ei
                ));
            }
        }

        String t = newTemp();
        List<IRExpr> temps = tis.stream().map(IRTemp::new).collect(Collectors.toList());
        IRStmt moveCall = new IRMove(
                new IRTemp(t),
                new IRCall(irnode.target(), temps)
        );

        stmts.add(moveCall);

        return new IRESeq(
                new IRSeq(stmts),
                new IRTemp(t)
        );
    }

    public IRNode lower(IRCJump irnode) {
        //don't worry about fallthroughs here
        IRExpr e = irnode.cond();

        if (e instanceof IRESeq) {
            IRESeq ireSeq = ((IRESeq) e);
            IRExpr eprime = ireSeq.expr();
            IRStmt s = ireSeq.stmt();
            return new IRSeq(
                    s,
                    new IRCJump(eprime, irnode.trueLabel(), irnode.falseLabel())
            );

        } else {
            return irnode;
        }
    }

    public IRNode lower(IRCompUnit irnode) {
        return irnode;
    }

    public IRNode lower(IRConst irnode) {
        //Constants are already canonical
        return irnode;
    }

    public IRNode lower(IRESeq irnode) {
        IRExpr expr = irnode.expr();
        if (expr instanceof IRESeq) {
            IRESeq es =(IRESeq) expr;
            IRStmt s1 = irnode.stmt();
            IRStmt s2 = es.stmt();
            IRExpr e = es.expr();
            return new IRESeq(new IRSeq(s1, s2), e);
        }
        else {
            return irnode;
        }
    }

    public IRNode lower(IRExp irnode) {
        IRExpr e = irnode.expr();
        if (e instanceof IRESeq) {
            return ((IRESeq) e).stmt();
        }
        else return new IRSeq();
    }

    public IRNode lower(IRFuncDecl irnode) {
        IRSeq body = (IRSeq) irnode.body();
        //lower the body
        body = (IRSeq) this.visit(body);
        return new IRFuncDecl(irnode.name(),
                (IRStmt) reorderBasicBlocks(body));
    }

    public IRNode lower(IRJump irnode) {
        IRExpr e = irnode.target();

        if (e instanceof IRESeq) {
            IRESeq ireSeq = (IRESeq) e;
            IRExpr eprime = ireSeq.expr();
            IRStmt s = ireSeq.stmt();

            return new IRSeq(
                    s,
                    new IRJump(eprime)
            );
        } else {
            return irnode;
        }
    }

    public IRNode lower(IRLabel irnode) {
        //Labels are already canonical
        return irnode;
    }

    public IRNode lower(IRMem irnode) {
        IRExpr e = irnode.expr();

        if (e instanceof IRESeq) {
            IRESeq ireSeq = (IRESeq) e;
            IRExpr eprime = ireSeq.expr();
            IRStmt s = ireSeq.stmt();

            return new IRESeq(
                    s,
                    new IRMem(eprime)
            );
        } else {
            return irnode;
        }
    }

    public IRNode lower(IRMove irnode) {
        IRExpr dest = irnode.target();
        IRExpr src = irnode.source();
        if (!(dest instanceof IRESeq || src instanceof IRESeq)) {
            return irnode;
        } else if (ifExprsCommute(dest, src)) {
            IRESeq destSeq;
            IRESeq srcSeq;
            IRExpr destprime;
            IRExpr eprime;
            IRStmt s1 = null;
            IRStmt s2 = null;

            if (dest instanceof IRESeq && src instanceof IRESeq) {
                destSeq = (IRESeq) dest;
                srcSeq = (IRESeq) src;
                destprime = destSeq.expr();
                eprime= srcSeq.expr();
                s1 = destSeq.stmt();
                s2 = srcSeq.stmt();
            } else if (dest instanceof IRESeq) {
                destSeq = (IRESeq) dest;
                destprime = destSeq.expr();
                eprime = src;
                s1 = destSeq.stmt();
            } else {
                srcSeq = (IRESeq) src;
                destprime = dest;
                eprime = srcSeq.expr();
                s2 = srcSeq.stmt();
            }

            List<IRStmt> stmts = new ArrayList<>();
            stmts.add(s1);
            stmts.add(s2);
            stmts.add(new IRMove(destprime, eprime));
            return new IRSeq(stmts);
        } else {
            IRESeq destSeq;
            IRESeq srcSeq;
            IRExpr destprime;
            IRExpr eprime;
            IRStmt s1 = null;
            IRStmt s2 = null;

            String t1 = newTemp();
            if (dest instanceof IRESeq && src instanceof IRESeq) {
                destSeq = (IRESeq) dest;
                srcSeq = (IRESeq) src;
                destprime = destSeq.expr();
                eprime = srcSeq.expr();
                s1 = destSeq.stmt();
                s2 = srcSeq.stmt();
            } else if (dest instanceof IRESeq) {
                destSeq = (IRESeq) dest;
                destprime = destSeq.expr();
                eprime = src;
                s1 = destSeq.stmt();
            } else {
                srcSeq = (IRESeq) src;
                destprime = dest;
                eprime = srcSeq.expr();
                s2 = srcSeq.stmt();
            }

            List<IRStmt> stmts = new ArrayList<>();
            stmts.add(s1);
            stmts.add(new IRMove(new IRTemp(t1), destprime));
            stmts.add(s2);
            stmts.add(new IRMove(new IRMem(new IRTemp(t1)), eprime));
            return new IRSeq(stmts);
        }
    }

    public IRNode lower(IRName irnode) {
        //Names are already canonical
        return irnode;
    }

    public IRNode lower(IRReturn irnode) {
        List<IRStmt> stmts = new ArrayList<>();
        List<IRExpr> newRets = new ArrayList<>();
        for (IRExpr e : irnode.rets()) {
            if (e instanceof IRESeq) {
                IRESeq es = (IRESeq) e;
                stmts.add(es.stmt());
                newRets.add(es.expr());
            }
            else newRets.add(e);
        }
        IRReturn retNode = new IRReturn(newRets);
        if (stmts.size() > 0) {
            stmts.add(retNode);
            return new IRSeq(stmts);
        }
        else {
            return retNode;
        }
    }

    public IRSeq lower(IRSeq irnode) {
        List<IRStmt> newStmts = new ArrayList<>();
        for (IRStmt s : irnode.stmts()) {
            IRStmt ls = (IRStmt) lower(s);
            if (ls instanceof IRSeq) {
                newStmts.addAll(((IRSeq) ls).stmts());
            }
            else {
                newStmts.add(ls);
            }
        }
        return new IRSeq(newStmts);
    }

    public IRNode lower(IRTemp irnode) {
        //Temps are already canonical
        return irnode;
    }

    public IRNode lower (IRStmt stmt) {
        if (stmt instanceof IRCJump) return lower((IRCJump) stmt);
        if (stmt instanceof IRExp) return lower((IRExp) stmt);
        if (stmt instanceof IRJump) return lower((IRJump) stmt);
        if (stmt instanceof IRMove) return lower((IRMove) stmt);
        if (stmt instanceof IRReturn) return lower((IRReturn) stmt);
        if (stmt instanceof IRSeq) return lower((IRSeq) stmt);
        return stmt;
    }

}
