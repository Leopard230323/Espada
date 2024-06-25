package generator.domain

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
    
    

/**
* generated by KtormGenerator
* @date 2024-02-12
*/
interface Tblfininstituteriskdata : Entity<Tblfininstituteriskdata> {
    companion object : Entity.Factory<Tblfininstituteriskdata>()
    var infoId: Long
    var iId: Long
    var timeId: Long?
    var liquidityRatio: Double?
    var loantoDepositRatio: Double?
    var liquidityCoverage: Double?
    var dPPM: Double?
    var nPL: Double?
    var vintage: Double?
    var specialMentionLoan: Double?
    var specialMentionLoanRatio: Double?
    var provisionCoverage: Double?
    var coreTier1CapitalAdequacyRatio: Double?
    var tier1CapitalAdequacyRatio: Double?
    var loanLossReserves: Double?
    var totalAssets: Double?
    var totalLiabilities: Double?
    var totalProfit: Double?
    var marketValue: Double?
    var totalLoans: Double?
    var shortTermLoan: Double?
    var mediumLongTermLoan: Double?
    var totalDeposits: Double?
    var roA: Double?
    var rateofReturnonCapital: Double?
    var nIM: Double?
    var costIncomeRatio: Double?
}

object Tblfininstituteriskdatas : Table<Tblfininstituteriskdata>("tblfininstituteriskdata") {
    val infoId = long("info_id").primaryKey().bindTo { it.infoId }
    val iId = long("i_id").bindTo { it.iId }
    val timeId = long("time_id").bindTo { it.timeId }
    val liquidityRatio = double("LiquidityRatio").bindTo { it.liquidityRatio }
    val loantoDepositRatio = double("LoantoDepositRatio").bindTo { it.loantoDepositRatio }
    val liquidityCoverage = double("LiquidityCoverage").bindTo { it.liquidityCoverage }
    val dPPM = double("DPPM").bindTo { it.dPPM }
    val nPL = double("NPL").bindTo { it.nPL }
    val vintage = double("Vintage").bindTo { it.vintage }
    val specialMentionLoan = double("SpecialMentionLoan").bindTo { it.specialMentionLoan }
    val specialMentionLoanRatio = double("SpecialMentionLoanRatio").bindTo { it.specialMentionLoanRatio }
    val provisionCoverage = double("ProvisionCoverage").bindTo { it.provisionCoverage }
    val coreTier1CapitalAdequacyRatio = double("CoreTier1CapitalAdequacyRatio").bindTo { it.coreTier1CapitalAdequacyRatio }
    val tier1CapitalAdequacyRatio = double("Tier1CapitalAdequacyRatio").bindTo { it.tier1CapitalAdequacyRatio }
    val loanLossReserves = double("LoanLossReserves").bindTo { it.loanLossReserves }
    val totalAssets = double("TotalAssets").bindTo { it.totalAssets }
    val totalLiabilities = double("TotalLiabilities").bindTo { it.totalLiabilities }
    val totalProfit = double("TotalProfit").bindTo { it.totalProfit }
    val marketValue = double("MarketValue").bindTo { it.marketValue }
    val totalLoans = double("TotalLoans").bindTo { it.totalLoans }
    val shortTermLoan = double("ShortTermLoan").bindTo { it.shortTermLoan }
    val mediumLongTermLoan = double("MediumLongTermLoan").bindTo { it.mediumLongTermLoan }
    val totalDeposits = double("TotalDeposits").bindTo { it.totalDeposits }
    val roA = double("RoA").bindTo { it.roA }
    val rateofReturnonCapital = double("RateofReturnonCapital").bindTo { it.rateofReturnonCapital }
    val nIM = double("NIM").bindTo { it.nIM }
    val costIncomeRatio = double("CostIncomeRatio").bindTo { it.costIncomeRatio }
}



val Database._tblfininstituteriskdatas get() = this.sequenceOf(Tblfininstituteriskdatas)
