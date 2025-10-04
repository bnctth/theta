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

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaState;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class XtaAndAnIntInitFunc<S extends State, P extends Prec> implements InitFunc<XtaAndAnIntState<S>, P> {
    private final InitFunc<XtaState<S>, ? super P> initFunc;

    protected XtaAndAnIntInitFunc(final InitFunc<XtaState<S>, ? super P> initFunc) {
        this.initFunc = checkNotNull(initFunc);
    }

    @Override
    public final Collection<XtaAndAnIntState<S>> getInitStates(final P prec) {
        return initFunc.getInitStates(prec)
                .stream()
                .map(state -> new XtaAndAnIntState<>(state, defaultR()))
                .toList();
    }

    protected abstract int defaultR();
}
