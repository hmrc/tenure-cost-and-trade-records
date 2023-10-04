
# tenure-cost-and-trade-records

This is service provides backend services between the frontend service and sending submitted data to the VOA.

This service is still under development but hoping to move to a limited private beta in November 2023. This is a limited private beta as we are not able to integrate with the new VOA systems until late 2024.

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
