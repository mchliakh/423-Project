#!/bin/bash

java app.server.Server_AOM M 0 &
java app.server.Server_AOM M 3 &
java app.server.Server_AOM M 2 &
java app.test.TestCase3
