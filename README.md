# Cells API v2 SDK for Kotlin Multiplatform

[![Wire logo](https://github.com/wireapp/wire/blob/master/assets/header-small.png?raw=true)](https://wire.com/jobs/)

This repository is part of the source code of Wire. You can find more information at [wire.com](https://wire.com) or by contacting opensource@wire.com.

For licensing information, see the attached LICENSE file and the list of third-party licenses at [wire.com/legal/licenses/](https://wire.com/legal/licenses/).

No license is granted to the Wire trademark and its associated logos, all of which will continue to be owned exclusively by Wire Swiss GmbH. Any use of the Wire trademark and/or its associated logos is expressly prohibited without the express prior written consent of Wire Swiss GmbH.

## Overview

We use the swagger specification that is maintained in [Cells](https://github.com/pydio/cells) main repository and [OpenAPI generator](https://openapi-generator.tech/docs/generators/kotlin/) to generate a standard Kotlin Multiplatform SDK.
The generated code can be found in this module, it should not be manually touched / modified.

## Usage

We publish the library as a standard Artifact in Maven Central via Sonatype

## Tests 

We provide a few unit test to showcase the use of the API.

To run them, simply define your target server URL and a PAT for a user that has access in RW to a workspace that has versioning enabled like this:

```properties
test.targer_server_url=
test.targer_server_pat=
```
