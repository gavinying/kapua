/*******************************************************************************
 * Copyright (c) 2011, 2017 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.device.call.message.kura.data;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.DeviceMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;

/**
 * Kura device message implementation.
 */
public class KuraDataMessage extends KuraMessage<KuraDataChannel, KuraDataPayload> implements DeviceMessage<KuraDataChannel, KuraDataPayload> {

    private static final long serialVersionUID = 1L;

    protected KuraDataChannel channel;
    protected Date timestamp;
    protected KuraDataPayload payload;

    /**
     * Constructor
     */
    public KuraDataMessage() {
        super();
    }

    /**
     * Constructor
     * 
     * @param channel
     * @param timestamp
     * @param payload
     */
    public KuraDataMessage(KuraDataChannel channel, Date timestamp, KuraDataPayload payload) {
        this();
        this.channel = channel;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    @Override
    public KuraDataChannel getChannel() {
        return channel;
    }

    @Override
    public KuraDataPayload getPayload() {
        return payload;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setChannel(KuraDataChannel channel) {
        this.channel = channel;
    }

    @Override
    public void setPayload(KuraDataPayload payload) {
        this.payload = payload;
    }

    /**
     * Set the message timestamp
     * 
     * @param timestamp
     */
    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
