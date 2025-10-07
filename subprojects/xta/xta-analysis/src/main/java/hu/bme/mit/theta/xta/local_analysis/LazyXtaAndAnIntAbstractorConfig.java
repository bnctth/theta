package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.ARG;
import hu.bme.mit.theta.analysis.algorithm.cegar.Abstractor;
import hu.bme.mit.theta.analysis.algorithm.cegar.AbstractorResult;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyState;

public class LazyXtaAndAnIntAbstractorConfig<SConcr extends State, SAbstr extends State, P extends Prec> {
    private final Abstractor<LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>>, XtaAndAnIntAction, P> lazyXtaAbstractor;
    private final P prec;
    private ARG<LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>>, XtaAndAnIntAction> arg;

    public LazyXtaAndAnIntAbstractorConfig(final Abstractor<LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>>, XtaAndAnIntAction, P> lazyXtaAbstractor, final P prec) {
        this.lazyXtaAbstractor = lazyXtaAbstractor;
        this.prec = prec;
    }

    public AbstractorResult check() {
        arg = lazyXtaAbstractor.createArg();
        return lazyXtaAbstractor.check(arg, prec);
    }

    public ARG<LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>>, XtaAndAnIntAction> getArg() {
        return arg;
    }
}
