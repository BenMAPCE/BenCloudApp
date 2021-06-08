cd /Applications/Postgres.app/Contents/Versions/11/bin;
./pg_dump --file "~/Downloads/bencloud-20210602-11.11.sql" --host "localhost" --port "5432" --username "benmap_system" --password --verbose --format=c --blobs "benmap"
