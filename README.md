# TWEET AUDIT
Tweet Audit is a CLI application that analyzes your tweets(X) archive using Gemini AI and flags tweets that go against your alignment criteria for manual deletion.

## FEATURES
- Resumable workflow - Can resume from your last progress without any worries
- Exponential backoff - Cooldown time that increases exponentially before retrying a request
- Dynamic Rate Limiter - You can insert your Gemini API RPM that matches your current plan to override the default configuration
- Saves flagged tweets for manual deletion in a csv file for you.
- Customizable evaluation criteria

## PREREQUISITES
- Java 21+
- Maven
- Twitter/X archive downloaded
- Gemini API Key

## HOW IT WORKS
- Loads your alignment criteria 
- Parses Twitter/X archive and loads previously processed tweets
- Sends unprocessed tweets to Gemini for evaluation.
- Parses flagged tweets to CSV
- Saves processed tweets after each response from Gemini

## SETUP PROCESS
- Get your Twtitter/X archive from Settings. This can take up to 24-48 hours
- Get Gemini API Key: [Google AI Studio](https://aistudio.google.com/apikey).
- Follow the instructions here to set your environment variables: [Setting Env. variables](https://ai.google.dev/gemini-api/docs/api-key). Use environment variable __GOOGLE_API_KEY__ as this is what the application recognizes.

## USAGE
- Clone the GitHub repository
```bash
git clone https://github.com/olujobii/tweet-audit.git
```

- Navigate to the root directory of the project and create a folder `data`

>__NOTE:  It is crucial to have this folder in the root directory of the project as this is where the application reads your Twiiter/X archive, alignment criteria, stores your processed tweets and the tweet flagged for manual deletion.__

- From the root directory, build the project
```maven
mvn clean package
```

- Run the application
```bash
java -jar target/tweet-audit-1.0-SNAPSHOT.jar <archive_path.js> <config_path.json> <output.csv> [--rpm]
```
- archive_path.js: Represents file name of the Twitter/X archive in `data` folder.
- config_path.json: Represents file name of the alignment criteria in `data` folder.
- output.csv: Represents name you will like to give the file that stores the flagged tweets in `data/output` folder.
- rpm: Default value is 15. If you are not on free tier plan, get your assigned RPM for the Gemini 3.1 flash lite model and pass as a command-line argument.

>__NOTE: rpm is an optional arguments. If you are not on the free tier in your Google AI studio, I recommend you enter the value of the Request Per Minute(RPM) for the Gemini 3.1 flash lite model. You can get this information here: [Rate Limit](https://aistudio.google.com/rate-limit?timeRange=last-90-days)__
- If you like to customize rpm (Example)
```bash
java -jar target/tweet-audit-1.0-SNAPSHOT.jar <archive_path.js> <config_path.json> <output.csv> --rpm 25
```
## CONFIGURATION
Create `criteria.json` (in data folder which is in the root directory). Configure to your taste
```json
{
  "forbiddenWords": [
    "shit",
    "swear"
  ],
  "professionalCheck": true,
  "tone": true,
  "excludePolitics": false
}
```

## OUTPUT
- output.csv: Tweets flagged for manual deletion by Gemini AI
- processed-tweets.txt: Tweets that has been processed by Gemini AI. Also ensures resumable workflow.

## PROJECT STRUCTURE
```bash
src/main/java/com/olujobii
                  /ai_client      # Gemini API integration
                  /config         # Criteria loader
                  /csv_parser     # CSV file parsing
                  /model          # domain
                  /orchestrator   # Application workflow
                  /tweet_parser   # Twitter/X archive parsing and processed tweets
                  /util           # helper class
                  Main.java       # Entry point
```