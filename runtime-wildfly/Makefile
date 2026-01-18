# ---- config ----
APP_NAME := statusapi
IMAGE := statusapi-wildfly
HTTP_PORT := 28080
DEBUG_PORT := 5005
WAR := target/$(APP_NAME).war
WILDFLY_IMAGE := quay.io/wildfly/wildfly:latest

# ---- helpers ----
.PHONY: clean build run debug stop logs

# ---- targets ----

## clean: remove image + stopped containers
clean:
	@echo ">> Cleaning docker image and stopped containers"
	-docker rm -f $$(docker ps -aq --filter ancestor=$(IMAGE)) 2>/dev/null || true
	-docker rmi -f $(IMAGE) 2>/dev/null || true

## build: mvn package + docker build
build:
	@echo ">> Building WAR"
	mvn -q -DskipTests package
	@echo ">> Building docker image $(IMAGE)"
	docker build -t $(IMAGE) .

test:
	@echo ">> Testing unit tests"
	mvn -q test

itest:
	@echo ">> Testing integration tests"
	src/test/it/status_player_blocked.sh

## run: run wildfly without debug
run:
	@echo ">> Running WildFly (no debug)"
	docker run --rm \
		-p $(HTTP_PORT):8080 \
		$(IMAGE)

## debug: run wildfly with remote debugging enabled
debug:
	@echo ">> Running WildFly with DEBUG on port $(DEBUG_PORT)"
	docker run --rm \
  		-p 28080:8080 \
		-p 5005:5005 \
  		-e JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
  		statusapi-wildfly


## run-dev: mount WAR directly (no rebuild image loop)
run-dev:
	@echo ">> Running WildFly with mounted WAR (fast dev loop)"
	docker run --rm \
		-p $(HTTP_PORT):8080 \
		-v "$(PWD)/$(WAR):/opt/jboss/wildfly/standalone/deployments/$(APP_NAME).war" \
		$(WILDFLY_IMAGE)

## debug-dev: mount WAR + debug (best dev loop)
debug-dev:
	@echo ">> Running WildFly with mounted WAR + DEBUG"
	docker run --rm \
		-p $(HTTP_PORT):8080 \
		-p $(DEBUG_PORT):5005 \
		-e JAVA_OPTS_APPEND="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
		-v "$(PWD)/$(WAR):/opt/jboss/wildfly/standalone/deployments/$(APP_NAME).war" \
		$(WILDFLY_IMAGE)

## stop: stop running containers for this app
stop:
	@echo ">> Stopping running containers"
	-docker stop $$(docker ps -q --filter ancestor=$(IMAGE)) 2>/dev/null || true

## logs: tail logs of running container
logs:
	@docker logs -f $$(docker ps -q --filter ancestor=$(IMAGE))
