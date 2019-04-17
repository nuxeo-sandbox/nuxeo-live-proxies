package org.nuxeo.ecm.operations;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.automation.core.operations.document.DeleteDocument;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 *
 */
@Operation(id = DeleteDocument.ID, category = Constants.CAT_DOCUMENT, label = "Delete proxies and document", description = "Delete all" +
        " proxies of a document then delete the document. Note that it will delete proxies regardless of user permissions on them," +
        " as long as the user can delete the source Document.")
public class DeleteProxiesAndDocument {

    public static final String ID = "LiveProxy.DeleteProxiesAndDocument";

    @Context
    protected CoreSession coreSession;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) {
        return run(doc.getRef());
    }

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentRef ref) {
        if (coreSession.canRemoveDocument(ref)) {
            this.deleteProxies(ref);
            coreSession.removeDocument(ref);
        }

        return null;
    }

    private void deleteProxies(DocumentRef ref) {
        new UnrestrictedSessionRunner(coreSession) {
            @Override
            public void run() {
                DocumentModelList proxies = session.getProxies(ref, null);
                if (proxies != null && !proxies.isEmpty()) {
                    DocumentModel doc = session.getDocument(ref);
                    for (DocumentModel proxy : proxies) {
                        if (!doc.isProxy() && proxy.getRef() != ref) {
                            session.removeDocument(proxy.getRef());
                        }
                    }
                }
            }
        }.runUnrestricted();
    }
}
