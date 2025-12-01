package hu.bme.mit.theta.analysis.zone;

import hu.bme.mit.theta.core.decl.Decls;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

public class LocalZoneOperationsTest {
    VarDecl<RatType> varA;
    VarDecl<RatType> varB;
    VarDecl<RatType> varC;

    VarDecl<RatType> ref1;
    VarDecl<RatType> ref2;


    DBM dbm1;
    DBM dbm2;

    List<DBM.ProcessDbmPair> pairs;

    @Before
    public void setUp() {
        varA = Decls.Var("A", RatType.getInstance());
        varB = Decls.Var("B", RatType.getInstance());
        varC = Decls.Var("C", RatType.getInstance());
        ref1 = Decls.Var("ref1", RatType.getInstance());
        ref2 = Decls.Var("ref2", RatType.getInstance());
        dbm1 = DBM.top(List.of(varA, varB, ref1));
        //dbm2 = DBM.top(List.of(varB, varC, ref2));
        dbm2 = DBM.top(List.of(varC, ref2));

        pairs = Stream.of(dbm1, dbm2).map(dbm -> new DBM.ProcessDbmPair(dbm.toString(), dbm)).toList();
    }

    @Test
    public void joinDbmTest() {
        Assert.assertEquals(List.of(dbm1, dbm2), DBM.joinDbms(pairs).extractDbms(pairs));
    }
}
