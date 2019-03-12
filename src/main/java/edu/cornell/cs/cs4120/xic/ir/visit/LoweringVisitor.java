package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class LoweringVisitor extends IRVisitor {
    private int tempcounter;

    private String newTemp() {
        return String.format("_lir_t%d", (tempcounter++));
    }

    private class BasicBlock {
        List<IRStmt> statements;
        boolean marked;

        BasicBlock () {
            statements = new ArrayList<>();
            marked = false;
        }

        void addStmt(IRStmt stmt) {
            statements.add(stmt);
        }

        IRNode getLastStmt() {
            return statements.get(statements.size()-1);
        }

        void mark() { marked = true; }
    }

    ArrayList<BasicBlock> basicBlocks;

    public LoweringVisitor(IRNodeFactory inf) {
        super(inf);
        tempcounter = 0;
        basicBlocks = new ArrayList<>();
        basicBlocks.add(new BasicBlock());
    }

    /**
     * Add a node to the basic block. If node is a label, return, or jump,
     * create a new block.
     * @param node node to be added to basic block
     */
    private void addNodeToBlock(IRStmt node) {
        int last = basicBlocks.size() - 1;
        if (basicBlocks.size() > 1 && node instanceof IRLabel) {
            BasicBlock newblock = new BasicBlock();
            newblock.addStmt(node);
            basicBlocks.add(newblock);
        } else if (node instanceof IRReturn || node instanceof IRJump || node instanceof IRCJump) {
            basicBlocks.get(last).addStmt(node);
            BasicBlock newblock = new BasicBlock();
            basicBlocks.add(newblock);
        } else {
            basicBlocks.get(last).addStmt(node);
        }
    }

    /**
     * Return all temps accessed by a list of nodes.
     * @param l list of IRNodes
     * @return list of IRTemps
     */
    private ArrayList<String> getTemps(List<IRNode> l) {
        ArrayList<String> temps = new ArrayList<String>();
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
        List<IRNode> e2Children = e1.aggregateChildren(new ListChildrenVisitor());
        e1Children.add(e2);
        //Check MEM
        for (IRNode n : e2Children) {
            if (n instanceof IRMem) return false;
        }
        //Check TEMPs
        ArrayList<String> temps = new ArrayList<String>();
        temps.addAll(getTemps(e1Children));
        temps.addAll(getTemps(e2Children));
        List<String> uniqList = temps.stream()
                .distinct()
                .collect(Collectors.toList());
        if(uniqList.size() < temps.size()) return false;
        return true;
    }

    /**
     * Given a label name, return its containing basic block.
     * @throws IllegalStateException if given nonexistent label.
     * @param lname the name of the label
     * @return the basic block containing the corresponding IRLabel node
     */
    private BasicBlock getBlockWithLabel(String lname) {
        for (BasicBlock b : basicBlocks) {
            IRStmt fst = b.statements.get(0);
            if (fst instanceof IRLabel) {
                IRLabel lbl = (IRLabel) fst;
                if (lname.equals(lbl.name())) return b;
            }
        }
        throw new IllegalStateException(lname + " is not a valid label");
    }

    /**
     * Reorder basic blocks so that jumps fall through whenever possible.
     * Called when lowering IRSeq nodes.
     * @param root current root of the IRNode tree
     * @return new root of IRNode tree, with basic blocks reordered
     */
    private IRNode reorderBasicBlocks(IRNode root) {
        for (int i = 0; i < basicBlocks.size(); i++) {
            BasicBlock b = basicBlocks.get(i);
            b.mark();
            if (b.getLastStmt() instanceof IRJump) {
                IRJump jmp = (IRJump) b.getLastStmt();
                IRExpr target = jmp.target();
                if (target instanceof IRName) {
                    IRName lname = (IRName) target;
                    BasicBlock fallThrough = getBlockWithLabel(lname.name());
                    if (i+1 < basicBlocks.size()) {
                        BasicBlock temp = basicBlocks.get(i+1);
                        if (!fallThrough.marked && !temp.marked) {
                            fallThrough.mark();
                            temp.mark();
                            basicBlocks.set(basicBlocks.indexOf(fallThrough), temp);
                            basicBlocks.set(i + 1, fallThrough);
                            b.statements.remove(basicBlocks.size() - 1);
                        }
                    }

                }
            }
        }
        List<IRStmt> stmts = new ArrayList<>();
        for (BasicBlock b : basicBlocks) {
            stmts.addAll(b.statements);
        }
        return new IRSeq(stmts);
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

            IRESeq ret = new IRESeq(
                    new IRSeq(s1, s2),
                    new IRBinOp(
                            irnode.opType(), e1, e2
                    ));
            return ret;
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

            IRESeq ret = new IRESeq(
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
            return ret;
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

        IRESeq ret = new IRESeq(
                new IRSeq(stmts),
                new IRTemp(t)
        );
        return ret;
    }

    public IRNode lower(IRCJump irnode) {
        IRExpr e = irnode.cond();

        if (e instanceof IRESeq) {
            IRESeq ireSeq = ((IRESeq) e);
            IRExpr eprime = ireSeq.expr();
            IRStmt s = ireSeq.stmt();

            IRSeq ret = new IRSeq(
                    new IRSeq(s, new IRCJump(eprime, irnode.trueLabel()),
                    new IRJump(new IRName(irnode.falseLabel())),
                    new IRLabel(irnode.falseLabel())
                    ));
            addNodeToBlock(ret);
            return ret;
        } else {
            addNodeToBlock(irnode);
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
            IRESeq ret = new IRESeq(new IRSeq(s1, s2), e);
            return ret;
        }
        else {
            return irnode;
        }
    }

    public IRNode lower(IRExp irnode) {
        IRExpr e = irnode.expr();
        if (e instanceof IRESeq) {
            IRStmt ret = ((IRESeq) e).stmt();
            addNodeToBlock(ret);
            return ret;
        }
        else return new IRSeq();
    }

    public IRNode lower(IRFuncDecl irnode) {
        return irnode;
    }

    public IRNode lower(IRJump irnode) {
        IRExpr e = irnode.target();

        if (e instanceof IRESeq) {
            IRESeq ireSeq = (IRESeq) e;
            IRExpr eprime = ireSeq.expr();
            IRStmt s = ireSeq.stmt();

            IRSeq ret = new IRSeq(
                    s,
                    new IRJump(eprime)
            );
            addNodeToBlock(ret);
            return ret;
        } else {
            addNodeToBlock(irnode);
            return irnode;
        }
    }

    public IRNode lower(IRLabel irnode) {
        //Labels are already canonical
        addNodeToBlock(irnode);
        return irnode;
    }

    public IRNode lower(IRMem irnode) {
        IRExpr e = irnode.expr();

        if (e instanceof IRESeq) {
            IRESeq ireSeq = (IRESeq) e;
            IRExpr eprime = ireSeq.expr();
            IRStmt s = ireSeq.stmt();

            IRESeq ret = new IRESeq(
                    s,
                    new IRMem(eprime)
            );
            return ret;
        } else {
            return irnode;
        }
    }

    public IRNode lower(IRMove irnode) {
        IRExpr dest = irnode.target();
        IRExpr src = irnode.source();
        if (!(dest instanceof IRESeq || src instanceof IRESeq)) {
            addNodeToBlock(irnode);
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
            IRSeq ret = new IRSeq(stmts);
            addNodeToBlock(ret);
            return ret;
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
            IRSeq ret = new IRSeq(stmts);
            addNodeToBlock(ret);
            return ret;
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
            IRSeq ret = new IRSeq(stmts);
            addNodeToBlock(ret);
            return ret;
        }
        else {
            addNodeToBlock(retNode);
            return retNode;
        }
    }

    public IRNode lower(IRSeq irnode) {
        List<IRStmt> newStmts = new ArrayList<>();
        for (IRStmt s : irnode.stmts()) {
            if (s instanceof IRSeq) {
                newStmts.addAll(((IRSeq) lower((IRSeq) s)).stmts());
            }
            else newStmts.add(s);
        }
        IRSeq ret = new IRSeq(newStmts);
        addNodeToBlock(ret);
        return ret;
        //return reorderBasicBlocks(ret);
    }

    public IRNode lower(IRTemp irnode) {
        //Temps are already canonical
        return irnode;
    }



}
