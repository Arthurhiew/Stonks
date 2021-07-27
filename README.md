# Stonks
In the last decade, the number of retail investors have increased tremendously thanks to the democratization of the stock market and apps such as Robinhood that allows “normal” people to invest into the stock market easily. However, most retail investors are not educated about the stock market and solely invest based on hype. This app aims to use the Discounted Cash Flow method to predict the intrinsic value of a company and identify whether the company is undervalued or not. However, the user of this app should keep in mind that they should not follow the calculations blindly and this app is by no means a financial advisor.

API used:https://rapidapi.com/apidojo/api/yahoo-finance1 \
Endpoints:
/get-chart : to graph the stock price
/get-financials: to get stock prices and other financial data
/get-balance-sheets and /get-cashflow: to calculate intrinsic values


MainActivity: The MainActivity displays the watchlist. The watchlist is a recycler view with stocks and stock information such as the symbol, price, intrinsic value, over/undervalue. It also will have a search bar at the top to search for stocks and transition to the SearchActivity.

StockDetailActivity: Users can navigate to this screen by clicking on the stock StockDetailActivity shows a graph to track a stock price, financial data, the stock’s intrinsic value, and the percentage of how much the stock is over/undervalued.

SearchActivity: SearchActivity displays search results when users input a keyword. It’ll show the stock’s ticker fds, company name, and a button to add to the watchlist.

## Authors
Arthur Hiew
Josiah Metz
Quan Nguyen




