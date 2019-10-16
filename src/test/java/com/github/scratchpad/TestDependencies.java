package com.github.scratchpad;

import org.assertj.core.api.JUnitJupiterSoftAssertions;
import org.immutables.value.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class TestDependencies {

    @RegisterExtension
    private final JUnitJupiterSoftAssertions softly = new JUnitJupiterSoftAssertions();

    @Test
    void testImmutables() {
        TestImmutable immutable = ImmutableTestImmutable.builder()
                .name("testing")
                .build();

        softly.assertThat(immutable)
                .as("check immutables is functional")
                .hasFieldOrPropertyWithValue("name", "testing");
    }

    @Value.Immutable
    interface TestImmutable {
        String getName();
    }
}
