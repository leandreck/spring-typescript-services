/**
 * Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leandreck.endpoints.processor.model;

/**
 * Configuration for customizing some aspects of the generated TypeScript files.
 * Is available in ftl-templates while generating Endpoints.
 */
public class PrintConfiguration {
    
    private final boolean useSuffixes;
    private final String suffixGet;
    private final String suffixHead;
    private final String suffixDelete;
    private final String suffixOptions;
    private final String suffixPatch;
    private final String suffixPost;
    private final String suffixPut;
    private final String suffixTrace;

    public PrintConfiguration(final boolean useSuffixes,
                              final String suffixGet,
                              final String suffixHead,
                              final String suffixDelete,
                              final String suffixOptions,
                              final String suffixPatch,
                              final String suffixPost,
                              final String suffixPut,
                              final String suffixTrace) {
        this.useSuffixes = useSuffixes;
        this.suffixGet = suffixGet;
        this.suffixHead = suffixHead;
        this.suffixDelete = suffixDelete;
        this.suffixOptions = suffixOptions;
        this.suffixPatch = suffixPatch;
        this.suffixPost = suffixPost;
        this.suffixPut = suffixPut;
        this.suffixTrace = suffixTrace;
    }

    /**
     * Returns wether the templates should use suffixes for Methods or not.
     * @return true if suffixes should be used.
     */
    public boolean isUseSuffixes() {
        return useSuffixes;
    }

    /**
     * Suffix for HTTP-GET Methods.
     * @return Suffix String.
     */
    public String getSuffixGet() {
        return suffixGet;
    }

    /**
     * Suffix for HTTP-HEAD Methods.
     * @return Suffix String.
     */
    public String getSuffixHead() {
        return suffixHead;
    }

    /**
     * Suffix for HTTP-DELETE Methods.
     * @return Suffix String.
     */
    public String getSuffixDelete() {
        return suffixDelete;
    }

    /**
     * Suffix for HTTP-OPTIONS Methods.
     * @return Suffix String.
     */
    public String getSuffixOptions() {
        return suffixOptions;
    }

    /**
     * Suffix for HTTP-PATCH Methods.
     * @return Suffix String.
     */
    public String getSuffixPatch() {
        return suffixPatch;
    }

    /**
     * Suffix for HTTP-POST Methods.
     * @return Suffix String.
     */
    public String getSuffixPost() {
        return suffixPost;
    }

    /**
     * Suffix for HTTP-PUT Methods.
     * @return Suffix String.
     */
    public String getSuffixPut() {
        return suffixPut;
    }

    /**
     * Suffix for HTTP-TRACE Methods.
     * @return Suffix String.
     */
    public String getSuffixTrace() {
        return suffixTrace;
    }
}
