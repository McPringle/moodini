*Moodini*
=========

[![Build Status](https://travis-ci.org/McPringle/moodini.svg?branch=master)](https://travis-ci.org/McPringle/moodini) [![Coverage Status](https://coveralls.io/repos/github/McPringle/moodini/badge.svg?branch=master)](https://coveralls.io/github/McPringle/moodini?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f5a5b7a6eeaf4609aedc50432a535bde)](https://www.codacy.com/app/McPringle/moodini?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=McPringle/moodini&amp;utm_campaign=Badge_Grade) [![Stories in Ready](https://badge.waffle.io/McPringle/moodini.png?label=ready&title=ready)](http://waffle.io/McPringle/moodini) [![gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg)](https://gitmoji.carloscuesta.me)

**An easy to use mood application. For more information please take a look at our [project website](https://moodini.ch/).**

*Copyright (C) 2016, 2017 Marcus Fihlon*

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

## Configuration

*Moodini* uses [MongoDB](https://wikipedia.org/wiki/MongoDB) to store it's data. Therefore you must provide the credentials for a running database instance through environment variables:

| Environment Variable | Description |
| -------------------- | ------------- |
| db_username          | The name of the user (login) for authorization. |
| db_password          | The password used to authorize the user. |
| db_host              | The host which runs the MongoDB instance |
| db_port              | The port which provides access to the MongoDB server. |
| db_name              | The name of the database to be used by *Moodini*. |

## Throughput

[![Throughput Graph](https://graphs.waffle.io/mcpringle/moodini/throughput.svg)](https://waffle.io/mcpringle/moodini/metrics/throughput)
