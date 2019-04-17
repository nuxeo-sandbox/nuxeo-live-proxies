package org.nuxeo.ecm.operations;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.proxy.nuxeo-live-proxies-core")
public class TestCreateLiveProxies {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldCallWithIdsParameter() throws OperationException {
        OperationContext ctx = new OperationContext(session);

        DocumentModel document = session.createDocumentModel("/", "simple-doc", "Document");
        document = session.createDocument(document);
        ctx.setInput(document);

        try {
            automationService.run(ctx, CreateLiveProxies.ID);
        }
        catch (OperationException ex) {
            return;
        }
        fail(CreateLiveProxies.ID + " should have failed because parameters 'ids' is missing");
    }

    @Test
    public void shouldCreateOneProxy() throws OperationException {
        DocumentModel sourceDoc = session.createDocumentModel("/", "source", "Document");
        sourceDoc = session.createDocument(sourceDoc);

        DocumentModel folder = session.createDocumentModel("/", "folder", "Folder");
        folder = session.createDocument(folder);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(sourceDoc);

        Map<String, Object> params = new HashMap<>();
        params.put("ids", folder.getId());

        automationService.run(ctx, CreateLiveProxies.ID, params);

        DocumentModel proxy = session.getDocument(new PathRef(folder.getPathAsString() + "/" + sourceDoc.getName()));
        assertNotNull(proxy);
        assertTrue(proxy.isProxy());
    }

    @Test
    public void shouldCreateSeveralProxiesForOneSourceDoc() throws OperationException {
        DocumentModel sourceDoc = session.createDocumentModel("/", "source", "Document");
        sourceDoc = session.createDocument(sourceDoc);

        DocumentModel folder1 = session.createDocumentModel("/", "folder1", "Folder");
        folder1 = session.createDocument(folder1);

        DocumentModel folder2 = session.createDocumentModel("/", "folder2", "Folder");
        folder2 = session.createDocument(folder2);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(sourceDoc);

        Map<String, Object> params = new HashMap<>();
        String ids = folder1.getId() + "," + folder2.getId();
        params.put("ids", ids);

        automationService.run(ctx, CreateLiveProxies.ID, params);

        DocumentModel proxy1 = session.getDocument(new PathRef(folder1.getPathAsString() + "/" + sourceDoc.getName()));
        assertNotNull(proxy1);
        assertTrue(proxy1.isProxy());
        DocumentModel proxy2 = session.getDocument(new PathRef(folder2.getPathAsString() + "/" + sourceDoc.getName()));
        assertNotNull(proxy2);
        assertTrue(proxy2.isProxy());
    }

    @Test
    public void shouldCreateAProxyForSeveralSourceDoc() throws OperationException {
        DocumentModel sourceDoc1 = session.createDocumentModel("/", "source1", "Document");
        sourceDoc1 = session.createDocument(sourceDoc1);

        DocumentModel sourceDoc2 = session.createDocumentModel("/", "source2", "Document");
        sourceDoc2 = session.createDocument(sourceDoc2);

        DocumentModelList sourceDocList = new DocumentModelListImpl();
        sourceDocList.add(sourceDoc1);
        sourceDocList.add(sourceDoc2);

        DocumentModel folder = session.createDocumentModel("/", "folder", "Folder");
        folder = session.createDocument(folder);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(sourceDocList);

        Map<String, Object> params = new HashMap<>();
        params.put("ids", folder.getId());

        automationService.run(ctx, CreateLiveProxies.ID, params);

        DocumentModel proxy1 = session.getDocument(new PathRef(folder.getPathAsString() + "/" + sourceDoc1.getName()));
        assertNotNull(proxy1);
        assertTrue(proxy1.isProxy());
        DocumentModel proxy2 = session.getDocument(new PathRef(folder.getPathAsString() + "/" + sourceDoc2.getName()));
        assertNotNull(proxy2);
        assertTrue(proxy2.isProxy());
    }

    @Test
    public void shouldCreateSeveralProxiesForSeveralSourceDoc() throws OperationException {
        DocumentModel sourceDoc1 = session.createDocumentModel("/", "source1", "Document");
        sourceDoc1 = session.createDocument(sourceDoc1);

        DocumentModel sourceDoc2 = session.createDocumentModel("/", "source2", "Document");
        sourceDoc2 = session.createDocument(sourceDoc2);

        DocumentModelList sourceDocList = new DocumentModelListImpl();
        sourceDocList.add(sourceDoc1);
        sourceDocList.add(sourceDoc2);

        DocumentModel folder1 = session.createDocumentModel("/", "folder1", "Folder");
        folder1 = session.createDocument(folder1);

        DocumentModel folder2 = session.createDocumentModel("/", "folder2", "Folder");
        folder2 = session.createDocument(folder2);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(sourceDocList);

        Map<String, Object> params = new HashMap<>();
        String ids = folder1.getId() + "," + folder2.getId();
        params.put("ids", ids);

        automationService.run(ctx, CreateLiveProxies.ID, params);

        DocumentModel proxy1 = session.getDocument(new PathRef(folder1.getPathAsString() + "/" + sourceDoc1.getName()));
        assertNotNull(proxy1);
        assertTrue(proxy1.isProxy());
        DocumentModel proxy2 = session.getDocument(new PathRef(folder1.getPathAsString() + "/" + sourceDoc2.getName()));
        assertNotNull(proxy2);
        assertTrue(proxy2.isProxy());
        DocumentModel proxy3 = session.getDocument(new PathRef(folder2.getPathAsString() + "/" + sourceDoc1.getName()));
        assertNotNull(proxy3);
        assertTrue(proxy3.isProxy());
        DocumentModel proxy4 = session.getDocument(new PathRef(folder2.getPathAsString() + "/" + sourceDoc2.getName()));
        assertNotNull(proxy4);
        assertTrue(proxy4.isProxy());
    }
}
