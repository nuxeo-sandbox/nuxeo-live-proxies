package org.nuxeo.ecm.operations;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.proxy.nuxeo-live-proxies-core")
public class TestCheckProxyExistence {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldReturnFalseIfNoProxy() throws OperationException {
        DocumentModel document = session.createDocumentModel("/", "simple-doc", "Document");
        document = session.createDocument(document);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(document);

        Boolean hasProxy = (Boolean) automationService.run(ctx, CheckProxyExistence.ID);

        Assert.assertFalse(hasProxy);
    }

    @Test
    public void shouldReturnTrueIfProxy() throws OperationException {
        DocumentModel document = session.createDocumentModel("/", "simple-doc", "Document");
        document = session.createDocument(document);

        DocumentModel folder = session.createDocumentModel("/", "folder", "Folder");
        folder = session.createDocument(folder);

        session.createProxy(document.getRef(), folder.getRef());

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(document);

        Boolean hasProxy = (Boolean) automationService.run(ctx, CheckProxyExistence.ID);

        Assert.assertTrue(hasProxy);
    }
}
