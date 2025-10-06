/*
 *  Copyright 2017 Budapest University of Technology and Economics
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.*;
import hu.bme.mit.theta.xta.analysis.XtaAnalysis;

public final class XtaAndAnIntAnalysis<S extends State, P extends Prec> implements Analysis<XtaAndAnIntState<S>, XtaAndAnIntAction, P> {
    private final PartialOrd<XtaAndAnIntState<S>> partialOrd;
    private final InitFunc<XtaAndAnIntState<S>, P> initFunc;
    private final TransFunc<XtaAndAnIntState<S>, XtaAndAnIntAction, P> transFunc;

    private XtaAndAnIntAnalysis(final XtaAnalysis<S, P> xtaAnalysis, final SystemTypeFactory<S, P> factory) {
        this.partialOrd = factory.createOrd(xtaAnalysis.getPartialOrd());
        this.initFunc = factory.createInitFunc(xtaAnalysis.getInitFunc());
        this.transFunc = XtaAndAnIntTransFunc.create(xtaAnalysis.getTransFunc());
    }

    public static <S extends State, P extends Prec> XtaAndAnIntAnalysis<S, P> create(final XtaAnalysis<S, P> xtaAnalysis, final SystemTypeFactory<S, P> factory) {
        return new XtaAndAnIntAnalysis<>(xtaAnalysis, factory);
    }

    @Override
    public PartialOrd<XtaAndAnIntState<S>> getPartialOrd() {
        return partialOrd;
    }

    @Override
    public InitFunc<XtaAndAnIntState<S>, P> getInitFunc() {
        return initFunc;
    }

    @Override
    public TransFunc<XtaAndAnIntState<S>, XtaAndAnIntAction, P> getTransFunc() {
        return transFunc;
    }

}
