package com.cibertec.sga.income.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.domain.repository.ICurrencyRepository;
import com.cibertec.sga.income.domain.error.IncomeError;
import com.cibertec.sga.income.domain.model.Income;
import com.cibertec.sga.income.domain.repository.IIncomeRepository;
import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.incomecategory.domain.repository.IIncomeCategoryRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.domain.repository.IReceiptRepository;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.domain.repository.IReceiptTypeRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeService implements IIncomeService {

    private static final String RECEIPT_TYPE_INCOME = "Income";

    private final IIncomeRepository incomeRepository;
    private final IIncomeCategoryRepository incomeCategoryRepository;
    private final ICurrencyRepository currencyRepository;
    private final IReceiptRepository receiptRepository;
    private final IReceiptTypeRepository receiptTypeRepository;

    public IncomeService(
        IIncomeRepository incomeRepository,
        IIncomeCategoryRepository incomeCategoryRepository,
        ICurrencyRepository currencyRepository,
        IReceiptRepository receiptRepository,
        IReceiptTypeRepository receiptTypeRepository
    ) {
        this.incomeRepository = incomeRepository;
        this.incomeCategoryRepository = incomeCategoryRepository;
        this.currencyRepository = currencyRepository;
        this.receiptRepository = receiptRepository;
        this.receiptTypeRepository = receiptTypeRepository;
    }

    @Override
    @Transactional
    public Result<Income, IncomeError> create(CreateIncomeCommand command) {
        var incomeCategoryOpt = incomeCategoryRepository.findByUuid(command.incomeCategoryUuid());
        if (incomeCategoryOpt.isEmpty()) {
            return Result.failure(new IncomeError.IncomeCategoryNotFound(command.incomeCategoryUuid().toString()));
        }
        IncomeCategory incomeCategory = incomeCategoryOpt.get();

        var currencyOpt = currencyRepository.findByUuid(command.currencyUuid());
        if (currencyOpt.isEmpty()) {
            return Result.failure(new IncomeError.CurrencyNotFound(command.currencyUuid().toString()));
        }
        Currency currency = currencyOpt.get();

        ReceiptType incomeType = receiptTypeRepository.findByName(RECEIPT_TYPE_INCOME).orElseThrow();
        Receipt receipt = receiptRepository.insert(
            Receipt.builder().receiptType(incomeType).amount(command.amount()).currency(currency).build()
        );

        Income income = incomeRepository.create(
            receipt, command.depositorName(), incomeCategory, currency, command.concept(), command.amount()
        );
        return Result.success(income);
    }

    @Override
    public Result<Income, IncomeError> findByUuid(UUID uuid) {
        return incomeRepository.findByUuid(uuid)
            .map(Result::<Income, IncomeError>success)
            .orElseGet(() -> Result.failure(new IncomeError.NotFound(uuid.toString())));
    }

    @Override
    public Page<Income> search(UUID incomeCategoryUuid, LocalDate date, Pageable pageable) {
        return incomeRepository.search(incomeCategoryUuid, date, pageable);
    }
}
