package com.wkerein.espada

import com.wkerein.espada.core.Database.defaultDatabase
import com.wkerein.espada.core.Espada.firstOrNull
import com.wkerein.espada.core.Espada.select
import generator.domain.Account
import generator.domain.Accounts
import generator.domain.Tblfininstituteriskdatas as RiskData
import generator.domain.Tblfininstitutionbasicinfos as BasicInfo
import generator.domain.Tbltimes as Times
import org.ktorm.dsl.*

object Methods {
    enum class Institutions(method: String) {
        Liquidity("流动性风险查询"),
        Credit("信用风险查询"),
        Adequacy("资本充足性风险查询"),
        Status("财务情况查询"),
        Portfolio("存贷款情况查询"),
    }
}

object arr {
    inline operator fun <reified T> get(vararg x: T) = arrayOf(*x)
}

fun queryInstitutions(
    iName: String? = null,
    year: Int? = null,
    quarter: Int? = null,
    area: String? = null,
    method: Methods.Institutions
) = defaultDatabase.from(BasicInfo)
    .leftJoin(RiskData, BasicInfo.iId eq RiskData.iId)
    .innerJoin(Times, RiskData.timeId eq Times.timeDimensionId)
    .select(
        Times.year,
        Times.quarter,
        *BasicInfo.columns.toTypedArray(),
        *when (method) {
            Methods.Institutions.Liquidity -> arr[
                RiskData.liquidityRatio,
                RiskData.loantoDepositRatio,
                RiskData.liquidityCoverage,
            ]

            Methods.Institutions.Credit -> arr[
                RiskData.totalAssets,
                RiskData.totalLiabilities,
                RiskData.totalProfit,
                RiskData.marketValue,
                RiskData.roA,
                RiskData.rateofReturnonCapital,
                RiskData.costIncomeRatio,
            ]

            Methods.Institutions.Adequacy -> arr[
                RiskData.dPPM,
                RiskData.nPL,
                RiskData.vintage,
                RiskData.specialMentionLoan,
                RiskData.specialMentionLoanRatio,
                RiskData.provisionCoverage,
            ]

            Methods.Institutions.Status -> arr[
                RiskData.coreTier1CapitalAdequacyRatio,
                RiskData.tier1CapitalAdequacyRatio,
                RiskData.loanLossReserves,
            ]

            Methods.Institutions.Portfolio -> arr[
                RiskData.totalLoans,
                RiskData.shortTermLoan,
                RiskData.mediumLongTermLoan,
                RiskData.totalDeposits,
                RiskData.nIM,
            ]
        }
    )
    .whereWithConditions {
        if (!iName.isNullOrBlank()) it += (BasicInfo.iName like "%$iName%")
        if (year != null) it += (Times.year eq year)
        if (quarter != null) it += (Times.quarter eq quarter)
        if (!area.isNullOrBlank()) it += (BasicInfo.area like "%$area%")
    }

fun getAccount(usr: Long) = defaultDatabase.from(Accounts)
    .select(Accounts)
    .where(Accounts.account eq usr)
    .iterator()
    .firstOrNull()
    ?.let { Accounts.createEntity(it) }

fun putAccount(account: Account) = defaultDatabase.insert(Accounts) {
    set(it.account, account.account)
    set(it.password, account.password)
    set(it.authority, account.authority)
}

