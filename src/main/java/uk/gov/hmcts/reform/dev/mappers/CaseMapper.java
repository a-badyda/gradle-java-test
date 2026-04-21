package uk.gov.hmcts.reform.dev.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.reform.dev.entities.Case;
import uk.gov.hmcts.reform.dev.models.CaseDTO;


@Mapper(componentModel = "spring",
    builder = @Builder(disableBuilder = true),
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CaseMapper {

    @Mapping(source = "createdDate", target = "createdDate")
    CaseDTO toDto(Case caseEntity);

    @Mapping(source = "createdDate", target = "createdDate")
    Case toEntity(CaseDTO caseDto);
}
