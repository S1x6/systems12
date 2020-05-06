# Systems12
В корень проекта необходимо положить архив RU-NVS.osm.bz2 (для первой задачи) и распакованный оттуда файл RU-NVS.osm (для второй)
## Для запуска первой задачи 
Необходимо передать аргументы "-t 1" 
`gradle run --args="-t 1"`
## Для второй 
Необходимо передать аргументы "-t 2"
`gradle run --args="-t 2"`
## Database
Поднимается в докере docker run -e POSTGRES_DB=main -e POSTGRES_USER=s1x6 -e POSTGRES_PASSWORD=password -d -p 5432:5432 postgres
