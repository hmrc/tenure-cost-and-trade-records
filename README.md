
# tenure-cost-and-trade-records

This is service provides backend services between the frontend service and sending submitted data to the VOA.

This service is still under development but hoping to move to a limited private beta in November 2023. This is a limited private beta as we are not able to integrate with the new VOA systems until late 2024.

## Rules to update SubmissionDraft DB model

1. Use Save as draft functionality to save several drafts in local Mongo DB before making any changes to SubmissionDraft DB model
2. Don’t rename or remove any existing property. Only add new properties.
3. On read model operations add extra code to read value from new property and from previous property. If new property value is empty read value from previous property.
4. On save model operations write value only to new property.
5. Test restoring draft saved by step 1 above.
6. Write down tenure-cost-and-trade-records app version in which extra code was added.
7. Check all production drafts that have previous property are expired on https://admin.tax.service.gov.uk/tctr-admin/drafts-per-version
8. Then remove extra code added by step 3 above.
9. Remove previous property from model. 

## Nomenclature

TCTR - Tenure Cost and Trade Records.

STaCI - Sent Trade and Cost Information.

FOR - form of return - forms used for users to send details of different types of business property.

## Dependencies

* A local Mongo DB instance needs to run locally
* You can start the dependencies in service manager by running:
>sm2 --start VOA_TCTR
>mongod

## Run the service
>sbt run

Then you can open the frontend in your browser the following url:
http://localhost:9526/send-trade-and-cost-information/login

* Service manager

```
sm2 --start VOA_TCTR_BACKEND
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
