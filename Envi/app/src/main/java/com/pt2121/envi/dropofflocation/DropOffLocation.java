/*
 * Copyright (c) 2015 Prat Tanapaisankit
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *   * Neither the name of The Intern nor the names of its contributors may
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE LISTED COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.pt2121.envi.dropofflocation;

import com.pt2121.envi.model.Loc;
import com.pt2121.envi.model.LocType;

import rx.Observable;

/**
 * Created by prt2121 on 1/20/15.
 */
public class DropOffLocation implements IFindDropOff {

    public static final String NAME = "Household Special Waste Drop-Off Site";

    @Override
    public Observable<Loc> getLocs() {
        Loc[] locs = {
                new Loc(NAME, "Hunts Point Ave New York, NY 10474",
                        40.820948, -73.890549,
                        LocType.DROPOFF),
                new Loc(NAME, "Gravesend New York, NY 11214",
                        40.591017, -73.977126,
                        LocType.DROPOFF),
                new Loc(NAME, "121st St New York, NY 11354",
                        40.771360, -73.847710,
                        LocType.DROPOFF),
                new Loc(NAME, "Muldoon Ave New York, NY 10312",
                        40.569652, -74.194829,
                        LocType.DROPOFF)
        };
        return Observable.from(locs);
    }
}
