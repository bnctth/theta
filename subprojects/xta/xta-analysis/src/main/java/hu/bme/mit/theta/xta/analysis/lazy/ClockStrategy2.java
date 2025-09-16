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
package hu.bme.mit.theta.xta.analysis.lazy;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public final class ClockStrategy2 {

    public enum ClockStrategy {
        LU, FWITP, BWITP;
    }

    public enum ZoneRepresentation {
        Global, Local, LocalSyncSub
    }


    private static final Collection<ClockStrategy2> VALID_DATA_STRATEGIES = List.of(

    );

    private final ClockStrategy clockStrategy;
    private final ZoneRepresentation zoneRepresentation;

    public ClockStrategy2(ClockStrategy clockStrategy) {
        this.clockStrategy = clockStrategy;
        this.zoneRepresentation = ZoneRepresentation.Global;
    }

    public ClockStrategy2(ClockStrategy clockStrategy, ZoneRepresentation zoneRepresentation) {
        this.clockStrategy = clockStrategy;
        this.zoneRepresentation = zoneRepresentation;
    }

    public ClockStrategy getClockStrategy() {
        return clockStrategy;
    }

    public ZoneRepresentation getZoneRepresentation() {
        return zoneRepresentation;
    }

    public static Collection<ClockStrategy2> getValidStrategies() {
        return VALID_DATA_STRATEGIES;
    }

    public boolean isValid() {
        return VALID_DATA_STRATEGIES.contains(this);
    }

    @Override
    public String toString() {
        return "DataStrategy2{" +
                "clockStrategy=" + clockStrategy +
                ", zoneRepresentation=" + zoneRepresentation +
                '}';
    }
}
