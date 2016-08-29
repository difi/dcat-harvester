printf 'Waiting for elasticsearchLogs container to respond'
until $(curl --output /dev/null --silent --head --fail http://elasticsearch:9200); do
    printf '.'
    sleep 2
done
printf '\nUploading Kibana configs\n'
curl -XPOST 'elasticsearch:9200/.kibana/_bulk?pretty' --data-binary "@/tmp/dashboard.json"
