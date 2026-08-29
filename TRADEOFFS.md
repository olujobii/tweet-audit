## ARCHITECTURE CHOICE
A component-based architecture, organising application into components with distinct responsibilities.

- csv_parser: Writes flagged tweets to csv file.
- config package: Reads user's alignment criteria.
- ai_client package: Handles Gemini AI calls, handles retries and rate limit.
- tweet_parser: Reads user's X archive and saves processed tweets to allow resumable workflow.
- util: Handles prompt building 
- Interface usage: Primary used around external dependencies to decouple application as implementation can easily be swapped in the future.

Overall, I chose predictability and simplicity over complexity.

## CONCURRENCY STRATEGY
The application main workflow is processed sequentially
- Loads your alignment criteria
- Parses Twitter/X archive and loads previously processed tweets
- Sends unprocessed tweets to Gemini for evaluation.
- Parses flagged tweets to CSV
- Saves processed tweets after each response from Gemini

Where batching is applied is at the point of sending the prompts with the tweets for Gemini to analyse. In each batch, a total of 5 tweets (or less) is built into the prompt. I applied batching here because
- As an X user who only has 437 tweets in my X archive, I was not pleased with the total processing time of the application (I spent roughly 25-30 minutes).

The idea behind using batching strategy here is to reduce the processing time of the application while preserving the predictability and simplicity of the application

### WHY BATCHING OVER FULL ASYNC
With full async
- Easier to exceed Gemini AI RPM limits
- Harder retry coordination and error handling
- Harder to implement resumable workflow and track processed tweets.

For an application of this size and use-case, the complexity is not worth it.

## ERROR HANDLING
The retry mechanism is used in handling errors in this application, with the implementation of a capped exponential backoff with jitter (a maximum of 5 attempts). Requests are only retried on:
- 429 error - Temporary rate limit as waiting may solve the problem
- 5xx error - Server-side/transient failures as retrying may succeed

## PERFORMANCE VS SAFETY TRADE-OFFS

The trade-offs here were primarily around I/O.

- Processed tweets are saved after every successful Gemini response, at the cost of more frequent disk writes.
- Flagged tweets are also saved immediately once identified by Gemini.

Overall, safety and reliability are prioritised over performance, as the additional I/O overhead is acceptable.


## WHY I CHOSE JAVA AS MY SPECIFIC LANGUAGE
I picked Java based on safety, ecosystem support and majorly, my desired career path.
- Java's type safety catches type mismatch at compile time. This is a huge benefit considering the fact that the application involved parsing JSON and I did not have to wait until runtime to identify the error.
- Java comes with a massive ecosystem support and libraries like Gson and OpenCSV, allowing me to focus on the business logic and not worry about implementing my own parsing logic.
- As someone who is working as an application support officer in a FinTech industry, I want to go a level deeper in understanding and building the systems that power this industry. Java or C# is the recommended choice here, but I chose Java.