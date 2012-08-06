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

import java.io.IOException;
import cowj.java.io.Reader;

/**
 * Encapsulates the contents of a <code>text/*</code> entity body.
 */
public abstract class TextBody extends SingleBody {

    /**
     * Sole constructor.
     */
    protected TextBody() {
    }

    /**
     * Returns the MIME charset of this text body.
     * 
     * @return the MIME charset.
     */
    public abstract String getMimeCharset();

    /**
     * Gets a <code>Reader</code> which may be used to read out the contents
     * of this body.
     * 
     * @return the <code>Reader</code>.
     * @throws java.io.IOException
     *             on I/O errors.
     */
    public abstract Reader getReader() throws IOException;

}
