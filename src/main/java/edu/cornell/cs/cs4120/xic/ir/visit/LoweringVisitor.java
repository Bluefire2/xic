package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

        BasicBlock (List<IRStmt> stmts) {
            statements = stmts;
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
        int last = Math.max(basicBlocks.size() - 1, 0);
        if (basicBlocks.get(last).statements.size() > 0 && node instanceof IRLabel) {
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
        List<IRNode> e2Children = e1.aggregateChildren(new ListChildrenVisitor());
        e1Children.add(e2);
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

    /**
     * Given a label name, return its containing basic block.
     * @throws IllegalStateException if given nonexistent label.
     * @param lname the name of the label
     * @return the basic block containing the corresponding IRLabel node
     */
    private BasicBlock getBlockWithLabel(String lname) {
        for (BasicBlock b : basicBlocks) {
            if (b.statements.size() > 0) {
                IRStmt fst = b.statements.get(0);
                if (fst instanceof IRLabel) {
                    IRLabel lbl = (IRLabel) fst;
                    if (lname.equals(lbl.name())) return b;
                }
            }
        }
        throw new IllegalStateException(lname + " is not a valid label");
    }

    private boolean canDeleteLabel(String lname) {
        for (BasicBlock b : basicBlocks) {
            for (IRStmt s : b.statements) {
                if (s instanceof IRJump) {
                    IRJump j = (IRJump) s;
                    if (j.target() instanceof IRName) {
                        if ((((IRName) j.target())).name().equals(lname)) return false;
                        }
                    }
                if (s instanceof IRCJump) {
                    IRCJump j = (IRCJump) s;
                    if (j.trueLabel().equals(lname)) return false;
                }
                }
            }
        return true;
    }

    /**
     * Reorder basic blocks so that jumps fall through whenever possible.
     * Called when lowering IRCJump nodes.
     * @param root current root of the IRNode tree
     * @return new root of IRNode tree, with basic blocks reordered
     */
    public IRNode reorderBasicBlocks(IRNode root) {
        for (int i = 0; i < basicBlocks.size(); i++) {
            BasicBlock b = basicBlocks.get(i);
                if (b.statements.size() > 0 && b.getLastStmt() instanceof IRJump) {
                    IRJump jmp = (IRJump) b.getLastStmt();
                    IRExpr target = jmp.target();
                    if (target instanceof IRName) {
                        IRName lname = (IRName) target;
                        BasicBlock fallThrough = getBlockWithLabel(lname.name());
                        if (i + 1 >= basicBlocks.size()) {
                            basicBlocks.add(new BasicBlock());
                        }
                        BasicBlock temp = basicBlocks.get(i + 1);
                        if (!fallThrough.marked && !temp.marked) {
                            b.mark();
                            fallThrough.mark();
                            basicBlocks.set(i, new BasicBlock(b.statements.subList(0, b.statements.size()-1)));
                            basicBlocks.set(basicBlocks.indexOf(fallThrough), temp);
                            int ftl = fallThrough.statements.size();
                            if (canDeleteLabel(lname.name())) {
                                basicBlocks.set(i+1, new BasicBlock(fallThrough.statements.subList(1, ftl)));
                            }
                            else basicBlocks.set(i + 1, fallThrough);
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
        IRExpr e = irnode.cond();
        IRSeq ret;

        if (e instanceof IRESeq) {
            IRESeq ireSeq = ((IRESeq) e);
            IRExpr eprime = ireSeq.expr();
            IRStmt s = ireSeq.stmt();

            if (irnode.hasFalseLabel()) {
                return new IRSeq(
                        s, new IRCJump(eprime, irnode.trueLabel()),
                        new IRJump(new IRName(irnode.falseLabel()))
                );
            }
            else return new IRSeq(s, new IRCJump(eprime, irnode.trueLabel()));

        } else {
            if(irnode.hasFalseLabel()) {
                return new IRSeq(
                        new IRCJump(e, irnode.trueLabel()),

                        new IRJump(new IRName(irnode.falseLabel()))
                );
            }
            else return new IRCJump(e, irnode.trueLabel());
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
        basicBlocks = new ArrayList<>();
        basicBlocks.add(new BasicBlock());
        IRStmt body = irnode.body();
        if (body instanceof IRSeq) {
            IRSeq seq = (IRSeq) body;
            for (IRStmt s : seq.stmts()) {
                IRStmt ls = (IRStmt) lower(s);
                if (ls instanceof IRSeq) {
                    for (IRStmt sprime : ((IRSeq) ls).stmts()) {
                        addNodeToBlock(sprime);
                    }
                }
                else addNodeToBlock(ls);
            }
        }
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

    public IRNode lower(IRSeq irnode) {
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
