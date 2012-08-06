/* Generated By:JavaCC: Do not edit this line. StructuredFieldParserConstants.java */
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
package cowj.org.apache.james.mime4j.field.structured.parser;

public interface StructuredFieldParserConstants {

  int EOF = 0;
  int STRING_CONTENT = 11;
  int FOLD = 12;
  int QUOTEDSTRING = 13;
  int WS = 14;
  int CONTENT = 15;
  int QUOTEDPAIR = 16;
  int ANY = 17;

  int DEFAULT = 0;
  int INCOMMENT = 1;
  int NESTED_COMMENT = 2;
  int INQUOTEDSTRING = 3;

  String[] tokenImage = {
    "<EOF>",
    "\"(\"",
    "\")\"",
    "\"(\"",
    "<token of kind 4>",
    "\"(\"",
    "\")\"",
    "<token of kind 7>",
    "<token of kind 8>",
    "\"\\\"\"",
    "<token of kind 10>",
    "<STRING_CONTENT>",
    "<FOLD>",
    "\"\\\"\"",
    "<WS>",
    "<CONTENT>",
    "<QUOTEDPAIR>",
    "<ANY>",
  };

}
