run:
	./gradlew run

up:
	docker-compose up

test:
	./gradlew test

check: test


docker-build:
	docker build -t lukaswire/ganymede .
