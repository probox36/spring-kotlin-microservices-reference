ALTER TABLE suborders_items
DROP CONSTRAINT suborders_items_suborder_id_fkey;

ALTER TABLE suborders_items
ADD CONSTRAINT suborders_items_suborder_id_fkey
FOREIGN KEY (suborder_id) REFERENCES suborders(id);