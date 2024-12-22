package com.smokpromotion.SmokProm.services.ai;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService {

    private final String key;

    public OpenAIService(@Value("${open_ai_key:}") String openAIkey){
        key = "sk_proj_"+openAIkey;
    }


        public String chat(String prompt) {

            ChatLanguageModel chatModel = OpenAiChatModel.builder()
                    .apiKey(key)
                    .modelName(GPT_4_O_MINI)
                    .build();

            return chatModel.chat(prompt);

        }

    class Image_Inputs {

        public void main(String[] args) {

            ChatLanguageModel chatModel = OpenAiChatModel.builder()
                    .apiKey(key) // Please use your own OpenAI API key
                    .modelName(GPT_4_O_MINI)
                    .maxTokens(50)
                    .build();

            UserMessage userMessage = UserMessage.from(
                    TextContent.from("What do you see?"),
                    ImageContent.from("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png")
            );

            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(userMessage)
                    .build();

            ChatResponse chatResponse = chatModel.chat(chatRequest);

            System.out.println(chatResponse.aiMessage().text());
        }
    }

    class Setting_Common_ChatRequestParameters {

        public void main(String[] args) {

            ChatRequestParameters defaultParameters = ChatRequestParameters.builder()
                    .modelName("gpt-4o")
                    .temperature(0.7)
                    .maxOutputTokens(100)
                    // there are many more common parameters, see ChatRequestParameters for more info
                    .build();

            ChatLanguageModel chatModel = OpenAiChatModel.builder()
                    .apiKey(key)
                    .defaultRequestParameters(defaultParameters)
                    .logRequests(true)
                    .build();

            ChatRequestParameters parameters = ChatRequestParameters.builder()
                    .modelName("gpt-4o-mini")
                    .temperature(1.0)
                    .maxOutputTokens(50)
                    .build();

            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(UserMessage.from("Tell me a funny story about Java"))
                    .parameters(parameters) // merges with and overrides default parameters
                    .build();

            ChatResponse chatResponse = chatModel.chat(chatRequest);

            System.out.println(chatResponse);
        }
    }

    class Setting_OpenAI_Specific_ChatRequestParameters {

        public void main(String[] args) {

            OpenAiChatRequestParameters defaultParameters = OpenAiChatRequestParameters.builder()
                    .seed(12345) // OpenAI-specific parameter
                    // there are many more OpenAI-specific parameters, see OpenAiChatRequestParameters for more info
                    .modelName("gpt-4o") // common parameter
                    .temperature(0.7) // common parameter
                    .maxOutputTokens(100) // common parameter
                    .build();

            ChatLanguageModel chatModel = OpenAiChatModel.builder()
                    .apiKey(key)
                    .defaultRequestParameters(defaultParameters)
                    .logRequests(true)
                    .build();

            OpenAiChatRequestParameters parameters = OpenAiChatRequestParameters.builder()
                    .seed(67890) // OpenAI-specific parameter
                    .modelName("gpt-4o-mini") // common parameter
                    .temperature(1.0) // common parameter
                    .maxOutputTokens(50) // common parameter
                    .build();

            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(UserMessage.from("Tell me a funny story about Java"))
                    .parameters(parameters) // merges with and overrides default parameters
                    .build();

            ChatResponse chatResponse = chatModel.chat(chatRequest);

            System.out.println(chatResponse);
        }
    }

}
