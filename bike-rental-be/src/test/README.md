## Index
- [Index](#index)
- [Prerequisites](#prerequisites)
- [API tests with Postman](#api-tests-with-postman)
  - [Exporting Postman collection](#exporting-postman-collection)
- [Java](#java)

## Prerequisites

- [Postman](https://www.postman.com/downloads/).

## API tests with Postman

1. Import the [Postman collection](postman/bike-rental-api.postman_collection.json) by dragging it into Postman:
![README.resources/importing_collection.png](README.resources/importing_collection.png)
2. Import your authorization token as a variable that is not exported to the collection, such as a global variable. Your token can be found as a header value prefixed with "Bearer " by opening the frontend in the browser and snooping on your requests to the backend using the DevTool's network tab. When importing your token into Postman, it should be without the "Bearer " prefix.
![README.resources/collection_token.png](README.resources/collection_token.png)
3. Pick and choose which tests you wish to run. Tests are split into endpoint (unit) and scenario (integration) tests.

### Exporting Postman collection

1. In Postman, select additional options of the collection, and navigate to the export menu;
![README.resources/exporting_collection_1.png](README.resources/exporting_collection_1.png)
2. Select the Collection v2.1 version and export it;
![README.resources/exporting_collection_2.png](README.resources/exporting_collection_2.png)
3. A file selection menu will appear. Select where to export and the file name of the exported collection.

## Java

Currently unimplemented.
