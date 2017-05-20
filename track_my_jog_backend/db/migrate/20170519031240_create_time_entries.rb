class CreateTimeEntries < ActiveRecord::Migration[5.0]
  def change
    create_table :time_entries do |t|
      t.date :date, null: false
      t.integer :distance, null: false
      t.integer :duration, null: false
      t.belongs_to :user, foreign_key: true, null: false

      t.timestamps
    end
  end
end
