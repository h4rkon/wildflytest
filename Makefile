# ---- config ----
MODULES := core adapter-xml adapter-wildfly
RUNTIME := runtime-wildfly

.PHONY: help all clean install build runtime docker run debug itest neo4j-up neo4j-down jq-scan

help:
	@echo "Targets:"
	@echo "  make install     -> mvn install on libs (core + adapters)"
	@echo "  make build       -> build runtime WAR"
	@echo "  make docker      -> docker build runtime image"
	@echo "  make run         -> run wildfly (no debug) via runtime Makefile"
	@echo "  make debug       -> run wildfly (debug) via runtime Makefile"
	@echo "  make itest       -> run integration test script via runtime Makefile"
	@echo "  make neo4j-up    -> start neo4j (for jQAssistant)"
	@echo "  make neo4j-down  -> stop neo4j"
	@echo "  make jq-scan     -> run jQAssistant scan (profile) in runtime module (or adjust)"
	@echo "  make clean       -> clean all modules + remove runtime image"
	@echo ""
	@echo "Shortcuts:"
	@echo "  make all         -> install + build + docker"

all: install build docker

# ---- maven builds ----
install:
	@set -e; \
	for m in $(MODULES); do \
	  echo ">> mvn install: $$m"; \
	  (cd $$m && mvn -q -DskipTests install); \
	done

build: install
	@echo ">> mvn package: $(RUNTIME)"
	@(cd $(RUNTIME) && mvn -q -DskipTests package)

clean:
	@set -e; \
	for m in $(MODULES) $(RUNTIME); do \
	  echo ">> mvn clean: $$m"; \
	  (cd $$m && mvn -q clean); \
	done
	@echo ">> docker image cleanup (runtime)"
	-$(MAKE) -C $(RUNTIME) clean || true

# ---- runtime delegation (uses runtime-wildfly/Makefile) ----
docker: build
	@$(MAKE) -C $(RUNTIME) build

run:
	@$(MAKE) -C $(RUNTIME) run

debug:
	@$(MAKE) -C $(RUNTIME) debug

itest:
	@$(MAKE) -C $(RUNTIME) itest

# ---- neo4j / jqassistant ----
neo4j-up:
	@echo ">> starting neo4j"
	@(cd neo4j && docker compose up -d)

neo4j-down:
	@echo ">> stopping neo4j"
	@(cd neo4j && docker compose down)

jq-scan: neo4j-up
	@echo ">> jQAssistant scan (runtime module)"
	@(cd $(RUNTIME) && mvn -q -DskipTests verify -Pjqassistant)
