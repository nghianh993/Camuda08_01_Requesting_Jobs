# Streaming vs Long Polling: Pros and Cons Comparison

| **Aspect**          | **Streaming (stream-enabled: true)**                                                                                                                                                    | **Long Polling (stream-enabled: false)**                                                                                                      |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| **Latency**         | - **Low latency:** Broker pushes job notifications immediately, providing near real-time updates.                                                                                       | - **Higher latency:** Must wait until the request times out or a new polling request is sent.                                                   |
| **Overhead**        | - **Lower overhead:** One persistent connection is maintained, reducing the cost of creating new requests.                                                                               | - **Higher overhead:** Frequent creation of new requests increases load on both client and broker.                                              |
| **Connection**      | - **Requires stable connection:** Needs a reliable and continuous network connection; unstable networks may disrupt the persistent stream.                                              | - **Resilient to network issues:** Each polling request is independent, so a failed request does not affect subsequent ones.                      |
| **Complexity**      | - **More complex:** Requires management of persistent connections, reconnection strategies, and error handling over a long-lived stream.                                               | - **Simpler setup:** Easier to implement since each request is isolated; simpler error handling and no need for reconnection management.         |
| **Resource Usage**  | - **Potential for higher resource consumption:** Persistent connections, if not managed well, might consume more resources on both the client and broker.                             | - **Generally lower per request:** Each request is short-lived, which can reduce the per-request resource usage, though overall overhead may increase. |

# ConfigStreaming.md

This document outlines the essential configurations for using **streaming** mode with the Zeebe Client. In streaming mode, the client opens a continuous connection to the broker to receive jobs in real-time. The document includes:

1. Necessary configurations (with descriptions, meanings, default values, and example modifications)
2. Worker-related configurations
3. A complete sample YAML configuration for a job worker in streaming mode

---

## 1. Necessary Configurations

### - **enabled**
- **Description:**  
  Determines whether the job worker is activated. When `enabled: true`, the worker registers and starts receiving jobs from the broker.
- **Default / Example Value:**
  - Example: `enabled: true`
- **Modification:**
  - Change to `false` if you want to temporarily disable the worker.

---

### - **stream-enabled**
- **Description:**  
  Specifies the mode of receiving jobs:
  - If `stream-enabled: true`, the client establishes a persistent streaming connection to the broker, receiving jobs as soon as they are available.
  - This mode bypasses the need for discrete polling requests.
- **Default / Example Value:**
  - Example: `stream-enabled: true`
- **Modification:**
  - Set to `false` to revert to long polling mode.

---

### - **stream-timeout**
- **Description:**  
  The maximum duration the streaming connection remains idle before timing out.  
  This parameter ensures that if no messages are sent for the specified period, the connection is considered stale and may be re-established.
- **Default / Example Value:**
  - Example: `stream-timeout: 3S` (3 seconds)
- **Modification:**
  - Increase if your environment has intermittent data flow or reduce if you prefer a shorter idle period before reconnection.

---

### - **request-timeout**
- **Description:**  
  Specifies the maximum time a request (such as the initial setup of the streaming connection) will wait for a response from the broker.
- **Default / Example Value:**
  - Example: `request-timeout: 1000` (1000ms or 1 second)
- **Modification:**
  - Adjust based on network conditions; for instance, use `500ms` for fast networks or `1500ms`/`2000ms` if additional time is needed.

---

## 2. Worker-Related Configurations

### - **max-jobs-active**
- **Description:**  
  Defines the maximum number of jobs that can be activated concurrently by the worker.
- **Default / Example Value:**
  - Example: `max-jobs-active: 3`
- **Modification:**
  - Increase (e.g., to `5` or `10`) if your system has sufficient resources or decrease to limit workload.

---

### - **timeout**
- **Description:**  
  The maximum time allowed for the worker to process a job and send a response (or complete the job) back to the broker.  
  If the job processing exceeds this time, it is considered timed out.
- **Default / Example Value:**
  - Example: `timeout: 1000` (1000ms, i.e., 1 second)
- **Modification:**
  - Increase to `2000` (2 seconds) or `5000` (5 seconds) for more complex processing tasks.

---

### - **auto-complete**
- **Description:**  
  Determines whether a job should be automatically marked as complete after processing.
  - If `auto-complete: true`, the job is automatically completed if no exception occurs.
  - If `auto-complete: false`, you must manually call `complete` or `fail`, allowing for custom error handling and retries.
- **Default / Example Value:**
  - Example: `auto-complete: false`
- **Modification:**
  - Set to `true` for a simpler process when no detailed error handling is required.

---

## 3. Complete Sample YAML Configuration for Streaming Mode

Below is a complete YAML configuration example for a job worker using streaming mode in Camunda 8:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        enabled: true                   # Activate the job worker
        stream-enabled: true            # Enable streaming mode (continuous connection)
        auto-complete: false            # Disable auto-complete; worker must manually complete jobs
        stream-timeout: 3S              # The streaming connection will timeout if idle for 3 seconds
        request-timeout: 1000           # Each request (e.g., to establish the stream) times out after 1000ms (1 second)
        max-jobs-active: 3              # Worker processes a maximum of 3 jobs concurrently
        timeout: 1000                   # Each job must be processed within 1000ms (1 second)
        fetch-variables: ''             # List of variables to fetch; leave empty if not needed
        force-fetch-all-variables: false  # Do not force fetching all variables
        name: ''                        # Worker name (default is used if left empty)
        tenant-ids: ''                  # Tenant IDs; leave empty if multi-tenancy is not used
        type: ''         # The job type that the worker will handle
      execution-threads: 1              # Number of threads for processing jobs
      prefer-rest-over-grpc: false      # Prefer gRPC over REST
