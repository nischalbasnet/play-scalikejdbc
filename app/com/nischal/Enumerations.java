package com.nischal;

import java.util.Enumeration;

public class Enumerations {
    /**
     * Enum for returning success for fail
     */
    enum Success {
        fail(0), ok(1);

        public int value;

        private Success(int value) {
            this.value = value;
        }
    }

    enum ResponseFormat {
        JSON, XML
    }

    enum ModelEvents {
        CREATED, UPDATED
    }

    enum RelationTypes {
        ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY
    }
}
