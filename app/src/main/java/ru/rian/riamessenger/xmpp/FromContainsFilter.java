package ru.rian.riamessenger.xmpp;

/**
 * $RCSfile: FromContainsFilter.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/25 21:41:42 $
 * <p>
 * Copyright 2003-2004 Jive Software.
 * <p>
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Stanza;

/**
 * Filters for packets where the "from" field contains a specified value.
 *
 * @author Matt Tucker
 */
class FromContainsFilter implements PacketFilter {

     String from;

    /**
     * Creates a "from" contains filter using the "from" field part.
     *
     * @param from the from field value the packet must contain.
     */
    public FromContainsFilter(String from) {
        if (from == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        this.from = from.toLowerCase();
    }

    @Override
    public boolean accept(Stanza stanza) {
        return stanza.getFrom() != null && stanza.getFrom().toLowerCase().indexOf(from) != -1;
    }
}