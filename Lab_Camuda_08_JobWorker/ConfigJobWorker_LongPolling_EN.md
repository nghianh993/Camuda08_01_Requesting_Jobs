# Streaming vs Long Polling: Pros and Cons Comparison

| **Aspect**          | **Streaming (stream-enabled: true)**                                                                                                                                                    | **Long Polling (stream-enabled: false)**                                                                                                      |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| **Latency**         | - **Low latency:** Broker pushes job notifications immediately, providing near real-time updates.                                                                                       | - **Higher latency:** Must wait until the request times out or a new polling request is sent.                                                   |
| **Overhead**        | - **Lower overhead:** One persistent connection is maintained, reducing the cost of creating new requests.                                                                               | - **Higher overhead:** Frequent creation of new requests increases load on both client and broker.                                              |
| **Connection**      | - **Requires stable connection:** Needs a reliable and continuous network connection; unstable networks may disrupt the persistent stream.                                              | - **Resilient to network issues:** Each polling request is independent, so a failed request does not affect subsequent ones.                      |
| **Complexity**      | - **More complex:** Requires management of persistent connections, reconnection strategies, and error handling over a long-lived stream.                                               | - **Simpler setup:** Easier to implement since each request is isolated; simpler error handling and no need for reconnection management.         |
| **Resource Usage**  | - **Potential for higher resource consumption:** Persistent connections, if not managed well, might consume more resources on both the client and broker.                             | - **Generally lower per request:** Each request is short-lived, which can reduce the per-request resource usage, though overall overhead may increase. |

# I. Long Polling

This document outlines the essential configurations for using **long polling** with the Zeebe Client. The content includes:

1. Necessary configurations (with description, meaning, default values, and example modifications)
2. Worker-related configurations
3. A complete sample YAML configuration for a job worker

---

## 1. Necessary Configurations

### - **enabled**
- **Description:**  
  Determines whether the job worker is activated. When `enabled: true`, the worker will register and receive jobs from the broker.
- **Default / Example Value:**
    - Default: In some setups, this might be `false` by default, but in this example we use `true` to activate the worker.
- **Modification:**
    - Change to `false` if you want to temporarily disable the worker.

---

### - **stream-enabled**
- **Description:**  
  Determines the mode of receiving jobs by the client.
    - When `stream-enabled: false`, the client uses long polling by sending individual requests at a set interval.
    - When `stream-enabled: true`, the client opens a continuous streaming connection to receive jobs immediately as they become available.
- **Default / Example Value:**
    - Example configuration: `stream-enabled: false`
- **Modification:**
    - Set to `true` if you wish to use streaming instead.

---

### - **poll-interval**
- **Description:**  
  The interval between consecutive polling requests sent to the broker.  
  After a request finishes (either by completion or timeout), the client waits for this duration before sending the next request.
- **Default / Example Value:**
    - Example configuration: `poll-interval: PT5S` means the client sends a request every 5 seconds.
- **Modification:**
    - For faster polling, you might use `PT3S`; for slower polling, you might use `PT60S`.

---

### - **request-timeout**
- **Description:**  
  Specifies the maximum time each polling request will wait for a response from the broker.  
  If no response is received within this timeframe, the request times out and is canceled.
- **Default / Example Value:**
    - Example configuration: `request-timeout: 1000` (or `1000ms`), which means each request waits for up to 1 second.
- **Modification:**
    - Depending on system responsiveness, you might adjust this to `500ms` if responses are quick, or `1500ms`/`2000ms` if more time is needed.
    - You can also use ISO-8601 duration format (e.g., `10S`).

---

## 2. Worker-Related Configurations

### - **max-jobs-active**
- **Description:**  
  Specifies the maximum number of jobs that can be activated concurrently by the worker.
- **Default / Example Value:**
    - Example configuration: `max-jobs-active: 3`
- **Modification:**
    - Increase this (e.g., to `5` or `10`) if your system has ample resources and needs to process more jobs concurrently; decrease it if you want to limit the load.

---

### - **timeout**
- **Description:**  
  The maximum time allowed for the worker to process a job and send a response (or complete the job) back to the broker.  
  If a job is not completed within this timeframe, it will time out.
- **Default / Example Value:**
    - Example configuration: `timeout: 1000` (1000ms, i.e., 1 second).
- **Modification:**
    - For more complex job processing, you might increase this to `2000` (2 seconds) or `5000` (5 seconds).
    - You can also specify the duration using ISO-8601 format (e.g., `10S`).

---

### - **auto-complete**
- **Description:**  
  Determines whether the job should be automatically completed after processing.
    - When `auto-complete: true`, the job is automatically marked as completed if no exception occurs.
    - When `auto-complete: false`, you must manually invoke `complete` or `fail`, allowing for error handling, retries, or logging.
- **Default / Example Value:**
    - Example configuration: `auto-complete: false`
- **Modification:**
    - Change to `true` if you prefer a simpler process that automatically completes jobs when there are no errors.

---

## 3. Complete Sample YAML Configuration

Below is a sample YAML configuration for long polling in Camunda 8:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        enabled: true                   # Activate the job worker
        stream-enabled: false           # Use long polling (do not use streaming)
        auto-complete: false            # Disable auto-complete; worker must manually complete jobs
        poll-interval: PT5S             # Poll every 5 seconds
        request-timeout: 1000           # Each request times out after 1000ms (1 second)
        max-jobs-active: 3              # Worker processes a maximum of 3 jobs concurrently
        timeout: 1000                   # Job lock timeout is 1000ms (1 second)
        fetch-variables: ''             # List of variables to fetch; leave empty if not needed
        force-fetch-all-variables: false  # Do not force fetching all variables
        name: ''                        # Worker name (defaults are used if left empty)
        tenant-ids: ''                  # Tenant IDs; leave empty if multi-tenancy is not used
        type: 'check_inventory'         # The job type the worker will handle
      execution-threads: 1              # Number of threads for processing jobs
      prefer-rest-over-grpc: false      # Prefer gRPC over REST


