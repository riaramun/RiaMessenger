package ru.rian.riamessenger.riaevents.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Roman on 7/8/2015.
 */

@AllArgsConstructor
public class RiaPresenceEvent {

    @Getter
    final String jid;

}
