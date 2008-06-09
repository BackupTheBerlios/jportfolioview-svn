/*
 * StockReportTable.fx
 *
 * Created on 17.05.2008, 02:15:35
 */

package PortfolioView;

import javafx.ui.*;
import java.util.Vector;
import java.lang.System;

import GnuCash.*;
import datasource.*;
/**
 * @author are
 */

class StockReportTable extends Table {
    static attribute bgColor1=Color { red: 0.95, green: 0.95, blue: 0.95 };
    static attribute bgColor2=Color { red: 1, green: 1, blue: 1 };
    static attribute fgColor2=Color { red: 0, green: 0, blue: 0 };
    static attribute fgColor1=Color { red: 1, green: 0, blue: 0 };
    
    attribute tableSelectModel:TableSelectModelClass;
    //attribute selectedTableCell=bind tableSelectModel.SelectedTableCell on replace {
    //    System.out.println("in Table cell selected: {selectedTableCell.text}");
    //}
    attribute selectedSymbol:String=bind tableSelectModel.SelectedTableCell.text;
    
    // this bug is nasty!!!
    // @todo: remove this, as soon the bug is fixed
    attribute gnucashUtil:GnuCashUtil on replace {
        System.out.println("update report, because file changed!");
        
        var acc=gnucashUtil.getStockAccountArr();
        accounts=acc;
    };
    attribute accounts:StockAccount[];
    
    override attribute rowSelectionAllowed=true;
    override attribute showVerticalLines=false;
    
    override attribute columns=[
    TableColumn {
        text: "Asset"
        width: 240
        alignment: HorizontalAlignment.LEADING
    }
    , TableColumn {
        text: "Symbol"
        width: 60
        alignment: HorizontalAlignment.CENTER
    }
    , TableColumn {
        text: "Shares"
        width: 60
        alignment: HorizontalAlignment.TRAILING
    }
    , TableColumn {
        text: "Einstand"
        width: 60
        alignment: HorizontalAlignment.TRAILING
    }
    , TableColumn {
        text: "Price"
        width: 60
        alignment: HorizontalAlignment.TRAILING
    }
    , TableColumn {
        text: "Balance"
        width: 60
        alignment: HorizontalAlignment.TRAILING
    }
    , TableColumn {
        text: "P/L"
        width: 60
        alignment: HorizontalAlignment.TRAILING
    }
    , TableColumn {
        text: "Yield"
        width: 60
        alignment: HorizontalAlignment.TRAILING
    }
    ];
    
    override attribute cells= bind for (a in accounts) [
    TableCell {
        text: a.getName()
        background: if (indexof a % 2 == 1) then bgColor1 else bgColor2
        foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
    }
    , TableCellClass {
        text: a.getSymbol()
        SelectModel: tableSelectModel
        background: if (indexof a % 2 == 1) then bgColor1 else bgColor2
        foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
    }
    , TableCell {
        text:  a.getStockBalanceStr()
        background: if (indexof a % 2 == 1) then bgColor1 else bgColor2
        foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
    }
    , TableCell {
        text: a.getAveragePriceStr()
        background: if (indexof a % 2 == 1) then bgColor1 else bgColor2
        foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
    }
    , TableCell {
        text: a.getLastPriceValueStr()
        background: if (indexof a % 2 == 1) then bgColor1 else bgColor2
        foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
    }
    , TableCell {
        text: a.getCashBalanceStr()
        background: if (indexof a % 2 == 1) then bgColor1 else bgColor2
        foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
    }
    , TableCell {
        text: a.getIncomeStr()
        background: if (indexof a % 2 == 1) then bgColor1 else bgColor2
        foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
    }
    , TableCell {
        text: a.getYieldStr()
        background: if (indexof a % 2 == 1) then bgColor1 else bgColor2
        foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
    }
    ];
}