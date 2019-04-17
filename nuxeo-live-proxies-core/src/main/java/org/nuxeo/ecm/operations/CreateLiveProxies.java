package org.nuxeo.ecm.operations;

import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.StringList;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 */
@Operation(id = CreateLiveProxies.ID, category = "LiveProxy", label = "Create live proxies", description = "Create one or several live proxies for one or several destinations.")
public class CreateLiveProxies {

    public static final String ID = "LiveProxy.CreateLiveProxies";

    @Context
    protected CoreSession session;

    @Param(name = "ids", required = true)
    protected StringList ids;

    @OperationMethod
    public void run(DocumentModelList input) {
        for (DocumentModel doc : input) {
            this.run(doc);
        }
    }

    @OperationMethod
    public void run(DocumentModel input) {
        DocumentRef inputRef = input.getRef();
        DocumentRef docRef;
        for (String id : ids) {
            docRef = new IdRef(id);
            if (!session.exists(docRef)) {
                throw new NuxeoException(String.format("Destination \"%s\" specified into operation not found", id));
            }
            session.createProxy(inputRef, docRef);
        }
    }
}
