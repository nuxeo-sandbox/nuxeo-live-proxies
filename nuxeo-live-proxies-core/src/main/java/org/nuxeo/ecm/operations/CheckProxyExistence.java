package org.nuxeo.ecm.operations;

import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
@Operation(id = CheckProxyExistence.ID, category = "LiveProxy", label = "Check proxy existence", description = "Checks if the document has any proxy(ies).")
public class CheckProxyExistence {

    public static final String ID = "LiveProxy.CheckProxyExistence";

    @Context
    protected CoreSession coreSession;

    @OperationMethod
    public boolean run(DocumentModel doc) {
        AtomicBoolean hasProxy = new AtomicBoolean(false);
        new UnrestrictedSessionRunner(coreSession) {
            @Override
            public void run() {
                hasProxy.set(hasProxy(session, doc));
            }
        }.runUnrestricted();

        return hasProxy.get();
    }

    @OperationMethod
    public boolean run(DocumentModelList docs) {
        AtomicBoolean hasProxy = new AtomicBoolean(false);
        new UnrestrictedSessionRunner(coreSession) {
            @Override
            public void run() {
                for (DocumentModel doc : docs) {
                    if (hasProxy(session, doc)) {
                        hasProxy.set(true);
                        return;
                    }
                }
            }
        }.runUnrestricted();

        return hasProxy.get();
    }

    private boolean hasProxy(CoreSession session, DocumentModel doc) {
        DocumentModelList proxies = session.getProxies(doc.getRef(), null);

        if (proxies == null) {
            return false;
        } else {
            if (doc.isProxy()) {
                // If the document is a proxy itself, it will be within the DocumentModelList, so only return true if there is another element in it
                return proxies.size() > 1;
            } else {
                return !proxies.isEmpty();
            }
        }
    }
}
