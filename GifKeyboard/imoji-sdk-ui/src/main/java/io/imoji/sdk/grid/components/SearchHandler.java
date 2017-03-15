/*
 * Imoji Android SDK UI
 * Created by engind
 *
 * Copyright (C) 2016 Imoji
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KID, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 */

package io.imoji.sdk.grid.components;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import io.imoji.sdk.ApiTask;
import io.imoji.sdk.ImojiSDK;
import io.imoji.sdk.objects.Category;
import io.imoji.sdk.objects.CategoryFetchOptions;
import io.imoji.sdk.objects.CollectionType;
import io.imoji.sdk.objects.Imoji;
import io.imoji.sdk.response.ApiResponse;
import io.imoji.sdk.response.CategoriesResponse;
import io.imoji.sdk.response.GenericApiResponse;
import io.imoji.sdk.response.ImojisResponse;

/**
 * Created by engind on 5/6/16.
 */
public abstract class SearchHandler {

    private final boolean autoSearchEnabled;

    private Stack<Pair<String, String>> historyStack;
    private ApiTask.WrappedAsyncTask<? extends ApiResponse> lastSearchTask;
    private String lastAutoSearchTerm;
    private boolean shouldAutoSearch = true;


    public SearchHandler(boolean autoSearchEnabled) {
        this.autoSearchEnabled = autoSearchEnabled;
        historyStack = new Stack<Pair<String, String>>() {

            @Override
            public Pair<String, String> push(Pair<String, String> object) {
                Pair<String, String> pair = super.push(object);
                onHistoryChanged();
                return pair;
            }

            @Override
            public synchronized Pair<String, String> pop() {
                Pair<String, String> popped = super.pop();
                onHistoryChanged();
                return popped;
            }

            @Override
            public void clear() {
                super.clear();
                onHistoryChanged();
            }
        };
    }

    public void searchTerm(Context context, final String term, final String title, final boolean addToHistory) {
        beforeSearchStarted();
        cancelLastTask();
        ApiTask.WrappedAsyncTask<ImojisResponse> task = new ApiTask.WrappedAsyncTask<ImojisResponse>() {
            @Override
            protected void onPostExecute(ImojisResponse imojisResponse) {
                List<SearchResult> newResults = new ArrayList<SearchResult>();
                for (Imoji imoji : imojisResponse.getImojis()) {
                    newResults.add(new SearchResult(imoji));
                }
                if (!imojisResponse.getRelatedCategories().isEmpty()) {
                    newResults.add(new SearchResult((Imoji) null));
                }
                for (Category c : imojisResponse.getRelatedCategories()) {
                    newResults.add(new SearchResult(c));
                }
                onSearchCompleted(newResults,
                        !imojisResponse.getRelatedCategories().isEmpty() ? imojisResponse.getImojis().size() : -1, false);

                if (addToHistory) {
                    historyStack.push(new Pair<>(term, title));
                }
            }
        };

        lastSearchTask = task;

        ImojiSDK.getInstance()
                .createSession(context.getApplicationContext())
                .searchImojis(term)
                .executeAsyncTask(task);
    }

    public void searchTrending(Context context) {
        beforeSearchStarted();
        cancelLastTask();
        ApiTask.WrappedAsyncTask<CategoriesResponse> task = new ApiTask.WrappedAsyncTask<CategoriesResponse>() {
            @Override
            protected void onPostExecute(CategoriesResponse categoriesResponse) {
                List<SearchResult> newResults = new ArrayList<SearchResult>();
                for (Category category : categoriesResponse.getCategories()) {
                    newResults.add(new SearchResult(category));
                }
                onSearchCompleted(newResults, -1, false);
            }
        };
        lastSearchTask = task;
        ImojiSDK.getInstance()
                .createSession(context.getApplicationContext())
                .getImojiCategories(new CategoryFetchOptions(Category.Classification.Trending))
                .executeAsyncTask(task);
    }

    public void autoSearch(final Context context, final String term) {
        if (autoSearchEnabled && !term.isEmpty()) {
            lastAutoSearchTerm = term;
            if (shouldAutoSearch) {
                shouldAutoSearch = false;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        shouldAutoSearch = true;
                        searchTerm(context, lastAutoSearchTerm, null, false);
                    }
                }, 500);
            }
        }
    }

    public void searchRecents(Context context) {
        searchCollections(context, CollectionType.Recents);
    }

    public void searchUserCollection(Context context) {
        searchCollections(context, null);
    }

    private void searchCollections(Context context, CollectionType collectionType) {
        beforeSearchStarted();
        cancelLastTask();
        ApiTask.WrappedAsyncTask<ImojisResponse> task = new ApiTask.WrappedAsyncTask<ImojisResponse>() {

            @Override
            protected void onPostExecute(ImojisResponse imojisResponse) {
                List<SearchResult> newResults = new ArrayList<SearchResult>();
                for (Imoji imoji : imojisResponse.getImojis()) {
                    newResults.add(new SearchResult(imoji));
                }
                onSearchCompleted(newResults, -1, true);
            }
        };
        lastSearchTask = task;
        ImojiSDK.getInstance()
                .createSession(context.getApplicationContext())
                .getCollectedImojis(collectionType)
                .executeAsyncTask(task);
    }

    public void addToRecents(Context context, Imoji imoji) {
        ImojiSDK.getInstance()
                .createSession(context.getApplicationContext())
                .markImojiUsage(imoji.getIdentifier(), null).executeAsyncTask(new ApiTask.WrappedAsyncTask<GenericApiResponse>() {
            @Override
            protected void onPostExecute(GenericApiResponse genericApiResponse) {

            }
        });
    }

    public Pair getFirstElement() {
        try {
            return historyStack.peek();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    private void cancelLastTask() {
        if (lastSearchTask != null) {
            lastSearchTask.cancel(true);
        }
    }

    public void clearHistory() {
        historyStack.clear();
    }

    public abstract void onSearchCompleted(List<SearchResult> newResults, int dividerPosition, boolean isRecents);

    public abstract void beforeSearchStarted();

    public abstract void onHistoryChanged();
}
