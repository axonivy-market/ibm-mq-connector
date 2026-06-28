<!--
Dear developer!     

When you create your very valuable documentation, please be aware that this Readme.md is not only published on github. This documentation is also processed automatically and published on our website. For this to work, the two headings "Demo" and "Setup" must not be changed. Do also not change the order of the headings. Feel free to add sub-sections wherever you want.
-->

# IMB MQ Connector

This connector integrates Axon Ivy with IBM MQ. It provides a callable sub to send one or more text messages to a queue and a demo setup that runs IBM MQ locally with Docker.

## Callable Subs

The connector ships the callable process MessageManagement with the following callable sub:

- send(MessagePushRequest)
  - Input parameter: messagePushRequest of type com.axonivy.connector.model.MessagePushRequest
    - queueName: the target IBM MQ queue name
    - payloads: one or more message payloads to send
  - Result: none
  - Behavior: each payload is sent to IBM MQ using the configured connection settings.

<!--
The explanations under "MY-RRODUCT-NAME" are displayed  e.g. for the Connector A-Trust here: https://market.axonivy.com/a-trust#tab-description   
-->

## Demo

The demo project contains a ready-to-run IBM MQ container and example processes for sending and processing messages. Start the local broker with Docker Compose from the demo module, import the demo project, and use the included sample flow to verify end-to-end message exchange.

<!--
We use all entries under the heading "Demo" for the demo-Tab on our Website, e.g. for the Connector A-Trust here: https://market.axonivy.com/a-trust#tab-demo  
-->

## Setup

Configure the IBM MQ connection properties in the project variables for your environment:

- host
- port
- channel
- queueManager
- username
- password
- mqDebugMessages
- skipListener

Example values are already provided in the connector configuration. If you want the connector to start listening for incoming messages automatically, leave skipListener disabled; otherwise set it to true.

Call the callable sub from your Ivy process with a MessagePushRequest data object to send messages to the selected queue.
<!--
The entries under the heading "Setup" are filled in this tab, e.g. for the Connector A-Trust here: https://market.axonivy.com/a-trust#tab-setup. 
-->

```
@variables.yaml@
```
