# nuxeo-live-proxies
Provide operations and actions to create and delete live proxies within WebUI

## Important Note

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

## Dependencies

`proxy-demo` Studio project: https://connect.nuxeo.com/nuxeo/site/studio/ide?project=proxy-demo
This Studio project add
* the `create-proxy` Polymer element used to display the dialog used for Proxy(ies) creation
* the `improved-delete-documents-button` to override default delete document button. This one will check existence of proxy(ies) before deleting a document
* slot contributions to find the above elements within WebUI

## Build and Install

Build with maven (at least 3.3)

```
mvn clean install
```
> Package built here: `/nuxeo-live-proxies-package/target`

> Install with `nuxeoctl mp-install <package>`

## Known limitations
This plugin is a work in progress, some things may not work. For instance "trash" 2-steps deletion is not properly handled yet, only hard-deletion (1-step) is.
