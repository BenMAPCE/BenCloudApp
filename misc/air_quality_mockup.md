# Manage Air Quality

## Display List of Air Quality Surfaces

| Name          | Pollutant | Grid Definition  |

| ------------- | --------- | ---------------- |

| Surface #1    | PM2.5     | CMAQ 12km Nation |

Note that the /api/v1/air-quality-data api endpoint only includes pollutant and grid definition ids. Just show those for now. Will add names later.

## Add Air Quality Surface
Present form to collection name, pollutant id, and grid definition id from the user.

TODO: Need to add GET /pollutants and /grid-definitions to the API to support these selections

Will POST to /air-quality-data as multipart/form-data

Fields are:

- name
- pollutantId
- gridId
- type (always "model" for now)
- file (a CSV file that the user will upload)

Example CSV files for model data are in the misc folder.

## Delete Air Quality Surface
Need to add delete to the ellipses menu next to each row

TODO: Need to add DELETE /air-quality-data/:id to the API to support this.