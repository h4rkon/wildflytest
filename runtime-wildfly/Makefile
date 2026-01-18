# runtime-wildfly/Makefile
#
# Targets:
#   make build        -> docker build image (expects WAR already built in target/)
#   make run          -> run WildFly container (no debug)
#   make debug        -> run WildFly container with remote debug on :5005
#   make run-dev      -> run WildFly official image with WAR mounted (fast dev loop)
#   make debug-dev    -> same as run-dev but with debug enabled
#   make logs         -> follow logs of the running container (by name)
#   make stop         -> stop running container (by name)
#   make itest        -> run shell integration test
#   make clean        -> remove container (if exists) + remove image

APP_NAME := statusapi
IMAGE := statusapi-wildfly
CONTAINER := statusapi-wildfly-local

HTTP_PORT := 28080
DEBUG_PORT := 5005

WAR := target/$(APP_NAME).war
WILDFLY_IMAGE := quay.io/wildfly/wildfly:latest

# JDWP: attach mode by default (suspend=n). For startup debug set suspend=y.
DEBUG_OPTS := -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

.PHONY: help build run debug run-dev debug-dev logs stop itest clean

help:
	@echo "runtime-wildfly Makefile"
	@echo ""
	@echo "Build & run:"
	@echo "  make build       - docker build $(IMAGE)"
	@echo "  make run         - run $(IMAGE) on http://localhost:$(HTTP_PORT)"
	@echo "  make debug       - run $(IMAGE) with JDWP on localhost:$(DEBUG_PORT)"
	@echo ""
	@echo "Dev loop (mount WAR, no docker rebuild):"
	@echo "  make run-dev     - run $(WILDFLY_IMAGE) with mounted WAR"
	@echo "  make debug-dev   - run $(WILDFLY_IMAGE) with mounted WAR + JDWP"
	@echo ""
	@echo "Ops:"
	@echo "  make logs        - follow logs (container name: $(CONTAINER))"
	@echo "  make stop        - stop container $(CONTAINER)"
	@echo "  make itest       - run integration test script"
	@echo "  make clean       - remove container + image"

# Build docker image (Dockerfile copies target/statusapi.war into deployments/)
build:
	@test -f "$(WAR)" || (echo "WAR not found: $(WAR). Run mvn package first." && exit 1)
	docker build -t $(IMAGE) .

# Run baked image (no debug)
run:
	@echo ">> Running $(IMAGE) as $(CONTAINER) (no debug)"
	docker run --rm --name $(CONTAINER) \
		-p $(HTTP_PORT):8080 \
		$(IMAGE)

# Run baked image with JDWP
debug:
	@echo ">> Running $(IMAGE) as $(CONTAINER) with JDWP on :$(DEBUG_PORT)"
	docker run --rm --name $(CONTAINER) \
		-p $(HTTP_PORT):8080 \
		-p $(DEBUG_PORT):5005 \
		-e JAVA_OPTS="$(DEBUG_OPTS)" \
		$(IMAGE)

# Run official WildFly image mounting the WAR (fast iteration, no image rebuild)
run-dev:
	@test -f "$(WAR)" || (echo "WAR not found: $(WAR). Run mvn package first." && exit 1)
	@echo ">> Running $(WILDFLY_IMAGE) as $(CONTAINER) with mounted WAR (no debug)"
	docker run --rm --name $(CONTAINER) \
		-p $(HTTP_PORT):8080 \
		-v "$(PWD)/$(WAR):/opt/jboss/wildfly/standalone/deployments/$(APP_NAME).war" \
		$(WILDFLY_IMAGE)

# Same as run-dev but with JDWP
debug-dev:
	@test -f "$(WAR)" || (echo "WAR not found: $(WAR). Run mvn package first." && exit 1)
	@echo ">> Running $(WILDFLY_IMAGE) as $(CONTAINER) with mounted WAR + JDWP on :$(DEBUG_PORT)"
	docker run --rm --name $(CONTAINER) \
		-p $(HTTP_PORT):8080 \
		-p $(DEBUG_PORT):5005 \
		-e JAVA_OPTS="$(DEBUG_OPTS)" \
		-v "$(PWD)/$(WAR):/opt/jboss/wildfly/standalone/deployments/$(APP_NAME).war" \
		$(WILDFLY_IMAGE)

logs:
	docker logs -f $(CONTAINER)

stop:
	-docker stop $(CONTAINER) 2>/dev/null || true

itest:
	./src/test/it/status_player_blocked.sh

clean:
	@echo ">> Cleaning runtime container/image"
	-docker rm -f $(CONTAINER) 2>/dev/null || true
	-docker rm -f $$(docker ps -aq --filter ancestor=$(IMAGE)) 2>/dev/null || true
	-docker rmi -f $(IMAGE) 2>/dev/null || true
