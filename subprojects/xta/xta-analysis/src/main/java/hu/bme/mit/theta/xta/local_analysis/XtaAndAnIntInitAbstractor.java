package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.lazy.InitAbstractor;
import hu.bme.mit.theta.xta.analysis.XtaState;

import static com.google.common.base.Preconditions.checkNotNull;

public final class XtaAndAnIntInitAbstractor<SConcr extends State, SAbstr extends State> implements InitAbstractor<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> {

    private final InitAbstractor<XtaState<SConcr>, XtaState<SAbstr>> initAbstractor;

    private XtaAndAnIntInitAbstractor(final InitAbstractor<XtaState<SConcr>, XtaState<SAbstr>> initAbstractor) {
        this.initAbstractor = checkNotNull(initAbstractor);
    }

    public static <SConcr extends State, SAbstr extends State> XtaAndAnIntInitAbstractor<SConcr, SAbstr> create(final InitAbstractor<XtaState<SConcr>, XtaState<SAbstr>> initAbstractor) {
        return new XtaAndAnIntInitAbstractor<>(initAbstractor);
    }

    @Override
    public XtaAndAnIntState<SAbstr> getInitAbstrState(final XtaAndAnIntState<SConcr> state) {
        return new XtaAndAnIntState<>(initAbstractor.getInitAbstrState(state.getState()), state.getR());
    }
}
