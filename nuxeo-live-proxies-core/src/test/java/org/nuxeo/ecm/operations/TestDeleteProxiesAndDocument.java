package org.nuxeo.ecm.operations;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.operations.document.DeleteDocument;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.proxy.nuxeo-live-proxies-core")
public class TestDeleteProxiesAndDocument {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldRemoveDocumentAndProxies() throws OperationException {
        DocumentModel document = session.createDocumentModel("/", "simple-doc", "Document");
        document = session.createDocument(document);

        assertNotNull(document);

        DocumentModel proxy = session.createProxy(document.getRef(), session.getParentDocumentRef(document.getRef()));
        assertNotNull(proxy);

        assertTrue(session.exists(proxy.getRef()));

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(document);

        automationService.run(ctx, DeleteDocument.ID);

        assertFalse(session.exists(document.getRef()));
        assertFalse(session.exists(proxy.getRef()));
    }

    @Test
    public void shouldBeAbleToDeleteAProxyItSelf() throws OperationException {
        DocumentModel document = session.createDocumentModel("/", "simple-doc", "Document");
        document = session.createDocument(document);

        DocumentModel folder = session.createDocumentModel("/", "folder", "Folder");
        folder = session.createDocument(folder);

        DocumentModel proxy = session.createProxy(document.getRef(), folder.getRef());
        session.save();

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(proxy);

        automationService.run(ctx, DeleteDocument.ID);

        assertTrue(session.exists(document.getRef()));
        assertFalse(session.exists(proxy.getRef()));
    }
}
