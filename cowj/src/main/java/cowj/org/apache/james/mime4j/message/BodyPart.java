/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package cowj.org.apache.james.mime4j.message;

/**
 * Represents a MIME body part  (see RFC 2045).
 */
public class BodyPart extends Entity {

    /**
     * Creates a new empty <code>BodyPart</code>.
     */
    public BodyPart() {
    }

    /**
     * Creates a new <code>BodyPart</code> from the specified
     * <code>BodyPart</code>. The <code>BodyPart</code> instance is initialized
     * with copies of header and body of the specified <code>BodyPart</code>.
     * The parent entity of the new body part is <code>null</code>.
     * 
     * @param other
     *            body part to copy.
     * @throws UnsupportedOperationException
     *             if <code>other</code> contains a {@link cowj.org.apache.james.mime4j.message.SingleBody} that
     *             does not support the {@link cowj.org.apache.james.mime4j.message.SingleBody#copy() copy()}
     *             operation.
     * @throws IllegalArgumentException
     *             if <code>other</code> contains a <code>Body</code> that
     *             is neither a {@link cowj.org.apache.james.mime4j.message.Message}, {@link cowj.org.apache.james.mime4j.message.Multipart} or
     *             {@link cowj.org.apache.james.mime4j.message.SingleBody}.
     */
    public BodyPart(BodyPart other) {
        super(other);
    }

}
