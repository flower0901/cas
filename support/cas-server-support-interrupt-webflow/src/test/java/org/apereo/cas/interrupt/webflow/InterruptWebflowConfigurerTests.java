package org.apereo.cas.interrupt.webflow;

import org.apereo.cas.config.CasInterruptConfiguration;
import org.apereo.cas.config.CasInterruptWebflowConfiguration;
import org.apereo.cas.web.flow.BaseWebflowConfigurerTests;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.engine.Flow;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link InterruptWebflowConfigurerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Import({
    CasInterruptConfiguration.class,
    CasInterruptWebflowConfiguration.class,
    BaseWebflowConfigurerTests.SharedTestConfiguration.class
})
@TestPropertySource(properties = "cas.interrupt.groovy.location=classpath:/interrupt.groovy")
@Tag("WebflowConfig")
public class InterruptWebflowConfigurerTests extends BaseWebflowConfigurerTests {
    @Test
    public void verifyOperation() {
        assertFalse(casWebflowExecutionPlan.getWebflowConfigurers().isEmpty());
        val flow = (Flow) this.loginFlowDefinitionRegistry.getFlowDefinition(CasWebflowConfigurer.FLOW_ID_LOGIN);
        assertNotNull(flow);
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_FINALIZE_INTERRUPT_ACTION));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_FINISHED_INTERRUPT));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_INQUIRE_INTERRUPT_ACTION));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_INTERRUPT_VIEW));
    }
}
