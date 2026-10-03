# Parrot Chat System (PCS)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![build](https://github.com/tima2015/parrot-server/actions/workflows/gradle.yml/badge.svg)](https://github.com/tima2015/parrot-server/actions/workflows/gradle.yml)
[![release](https://img.shields.io/github/v/release/tima2015/parrot-server?label=release)](https://github.com/tima2015/parrot-server/releases/)

PCS unites multiple game servers' chats into a single cross-server chat.
Example: Players in server#1 can directly chat to players in server#2.

This repository contains the **core server** (`parrot-server`).  
Client adapters for different games are listed below.

## Navigation

- [Client adapters](#client-adapters)
- [Getting started](#getting-started)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Roadmap](#roadmap)
- [Contributing](#contributing)

## Client adapters

If any client wants to connect to a PCS server, it needs an adapter.
Adapters send local chat messages to a PCS server and receive messages from it.

List of known adapters:

- oops, nothing at the moment...

## Getting started

1. Download latest parrot-server.jar from [release page](https://github.com/tima2015/parrot-server/releases/)
2. Open a terminal in the directory containing parrot-server.jar
3. Run `java -jar ./parrot-server.jar`

OR

If you have installed git, run following commands in terminal

```bash
git clone https://github.com/tima2015/parrot-server.git
cd parrot-server
./gradlew bootRun
```

## Prerequisites

- Java 17+
- Gradle 8+
- PostgreSQL (optional, for history)

## Configuration

Place `application.properties` next to the jar (or in a `config/` subdirectory)

| Property                       | Default value     | Description                                                                                                                                                                    |
|--------------------------------|-------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| history-writer.enabled         | fileMessageWriter | Comma-separated list of active writers. Available values: <br/>- fileMessageWriter, <br/>- DBMessageWriter                                                                     |
| history-writer.flush-schedule  | 10000             | Message history saving interval in ms                                                                                                                                          |
| history-writer.flush-threshold | 100	              | Max messages in buffer before forced flush                                                                                                                                     |
| history-writer.file.path	      | ./history/        | fileMessageWriter working directory                                                                                                                                            |
| spring.r2dbc.url               |                   | Database URL in format `r2dbc:<driver>://<host>:<port>/<database-name>`.<br/>Example: `r2dbc:postgresql://localhost:5432/testdb`. Required only if DBMessageWriter is selected |
| spring.r2dbc.username          |                   | Database username                                                                                                                                                              |
| spring.r2dbc.password          |                   | Database password                                                                                                                                                              |

## Roadmap

| Task               | Status    |
|--------------------|-----------|
| Message history    | ✅ Done    |
| Receive messages   | ✅ Done    |
| Broadcast messages | ✅ Done    |
| Message routing    | ⬜ planned |
| CI/CD pipeline     | ✅ Done    |
| Security           | 🟡 WIP    |
| Control panel      | ⬜ planned |
| Channels           | ⬜ planned |
| README.md          | ✅ Done    |
| Documentation      | ⬜ planned |

## Contributing

If you'd like to contribute, reach out via Telegram, VK, or email.