package ru.practicum.shareit.item;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemValidator{

    public static boolean isItemValid(Item item) {
        if(StringUtils.isBlank(item.getName())) {
            throw new ItemValidationException("Item Name validation error.");
        } else if (StringUtils.isBlank(item.getDescription())) {
            throw new ItemValidationException("Item Description validation error.");
        } else if (item.getAvailable() == null) {
            throw new ItemValidationException("Item Availability validation error.");
        } else {
            return true;
        }
    }

    public static boolean isItemDtoValid(ItemDto itemDto) {
        if(StringUtils.isBlank(itemDto.getName())) {
            throw new ItemValidationException("Item Name validation error.");
        } else if (StringUtils.isBlank(itemDto.getDescription())) {
            throw new ItemValidationException("Item Description validation error.");
        } else if (itemDto.getAvailable() == null) {
            throw new ItemValidationException("Item Availability validation error.");
        } else {
            return true;
        }
    }
}
