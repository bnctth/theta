
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
package hu.bme.mit.theta.xta.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import hu.bme.mit.theta.analysis.waitlist.LifoWaitlist;
import hu.bme.mit.theta.analysis.waitlist.Waitlist;
import hu.bme.mit.theta.xta.local_analysis.localzone.XtaLocalInitFunc;
import hu.bme.mit.theta.xta.local_analysis.localzone.XtaLocalTransFunc;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZonePrec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneState;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.dsl.XtaDslManager;

@RunWith(Parameterized.class)
public final class XtaLocalZoneTest {

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{

				{"/model/csma-2.xta"},

				{"/model/fddi-2.xta"},

				{"/model/fischer-2-32-64.xta"},

				{"/model/lynch-2-16.xta"},

				//{"/model/broadcast.xta"}, There are n+
				//
				// +o clock variables in this model! How does this work in global zone state?

		});
	}

	@Parameter(0)
	public String filepath;

	@Test
	public void test() throws FileNotFoundException, IOException {
		final InputStream inputStream = getClass().getResourceAsStream(filepath);
		final XtaSystem system = XtaDslManager.createSystem(inputStream);

		int bound = 10;
		int deepness = 0;

		LocalZonePrec localZonePrec = LocalZonePrec.of(system.getProcessClockMap());
		final XtaInitFunc<LocalZoneState, LocalZonePrec> systemProduct = XtaInitFunc.create(system, XtaLocalInitFunc.getInstance());
		XtaLts lts = XtaLts.create(system);
		final XtaTransFunc<LocalZoneState, LocalZonePrec> transFunc = XtaTransFunc.create(XtaLocalTransFunc.getInstance());
		Collection<XtaState<LocalZoneState>> initStates = systemProduct.getInitStates(localZonePrec);

		Waitlist<XtaState<LocalZoneState>> buffer = LifoWaitlist.create();
		buffer.addAll(initStates);

		ArrayList<XtaState<LocalZoneState>> statesToAdd = new ArrayList<>();

		while (deepness < bound) {
			while(!buffer.isEmpty()) {
				XtaState<LocalZoneState> currentState = buffer.remove();
				System.out.println(currentState);

				Collection<XtaAction> availableActions = lts.getEnabledActionsFor(currentState);
				for ( var action : availableActions) {
					statesToAdd.addAll(transFunc.getSuccStates(currentState, action, localZonePrec));
				}
			}
			deepness++;
			buffer.addAll(statesToAdd);
			statesToAdd.clear();
		}

		assert true;
	}

}
